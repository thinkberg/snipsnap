/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */
package org.snipsnap.admin.install;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;
import org.snipsnap.admin.util.CommandHandler;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.CreateDB;
import org.snipsnap.server.ApplicationLoader;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.user.User;
import org.snipsnap.util.Checksum;
import org.snipsnap.util.JarUtil;
import org.snipsnap.util.MckoiEmbeddedJDBCDriver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * Installer servlet that installs the application.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Installer extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    // get or create session and application object
    HttpSession session = request.getSession(false);
    if (null == session) {
      response.sendRedirect(SnipLink.absoluteLink(request, "/"));
      return;
    }
    Map errors = new HashMap();
    session.removeAttribute("errors");

    // get config from session
    Configuration serverConfig = (Configuration) session.getAttribute(CommandHandler.ATT_CONFIG);
    User admin = (User) session.getAttribute(CommandHandler.ATT_ADMIN);
    if (null == serverConfig || (serverConfig.getAdminLogin() != null && admin == null)) {
      response.sendRedirect(SnipLink.absoluteLink(request, "/"));
      return;
    }

    // create a new configuration object
    AppConfiguration config = new AppConfiguration();
    session.setAttribute("config", config);

    // change this to response.getWriter() to enable display on webpage
    PrintWriter out = new PrintWriter(System.out);
    writeMessage(out, "Installing SnipSnap ...");

    // set application name ...
    writeMessage(out, "Checking application name ...");
    String appName = request.getParameter("appname");
    if (null == appName || appName.length() == 0) {
      System.err.println("Installer: application name too short");
      errors.put("appname", "An application name is required!");
    }
    config.setName(appName != null ? appName : "");

    // set user name and email, check that information
    writeMessage(out, "Checking user name and password ...");
    config.setAdminLogin(request.getParameter("username"));
    if (null == config.getAdminLogin() || config.getAdminLogin().length() == 0) {
      System.err.println("Installer: user name too short");
      errors.put("login", "You must enter a user name with at least 3 characters!");
    }
    config.setAdminEmail(request.getParameter("email"));

    String password = request.getParameter("password");
    String password2 = request.getParameter("password2");
    if (null == password || password.length() == 0 || !password.equals(password2)) {
      System.err.println("Installer: passwords do not match");
      errors.put("password", "Passwords do not match!");
    } else {
      config.setAdminPassword(password);
    }

    writeMessage(out, "Checking server host and port information ...");
    // set host name and port if provided
    config.setHost(request.getParameter("host"));
    try {
      config.setPort(Integer.parseInt(request.getParameter("port")));
    } catch (NumberFormatException e) {
      System.err.println("Installer: port '" + request.getParameter("port") + "' is not a valid number");
      errors.put("port", "The port '" + request.getParameter("port") + "' is not a valid number!");
    }
    String context = request.getParameter("context");
    if(context != null && context.endsWith("/")) {
      config.setContextPath(context);
    } else if(context != null) {
      config.setContextPath(context+"/");
    } else {
      config.setContextPath("/");
    }

    if (errors.size() != 0) {
      sendError(session, errors, request, response);
      return;
    }

    InetAddrPort addrPort = new InetAddrPort();
    try {
      String host = config.getHost();
      if (host != null && host.length() > 0) {
        addrPort.setHost(host);
      } else {
        addrPort.setHost(InetAddrPort.__0_0_0_0);
      }
      addrPort.setPort(config.getPort());
    } catch (UnknownHostException e) {
      System.err.println("Installer: error binding host name: " + e);
      errors.put("host", "The host you entered is unknown, leave blank to bind server to the default host name.");
      sendError(session, errors, request, response);
      return;
    }

    Collection servers = Server.getHttpServers();
    Iterator it = servers.iterator();
    Server server = null;
    if (it.hasNext()) {
      server = (Server) it.next();

      try {
        server.addListener(new SocketListener(addrPort));
      } catch (Exception e) {
        System.err.println("Installer: error configuring socket listener: " + e);
        errors.put("host", "Unable to configure web server. Either you cannot run the web server on port "
                           + config.getPort() + " or there is something wrong with your host name.");
        sendError(session, errors, request, response);
        return;
      }
    }

    // create application root directory
    File webAppRoot = new File(serverConfig.getProperty(Configuration.SERVER_WEBAPP_ROOT) + "/" + config.getName());
    writeMessage(out, "Creating web application directories ...");
    if (!webAppRoot.mkdirs()) {
      System.err.println("Installer: error creating applications root directory");
      errors.put("fatal", "Unable to create applications root directory: " + webAppRoot);
      sendError(session, errors, request, response);
      return;
    }

    File appDir = new File(webAppRoot, "app");
    appDir.mkdir();
    File dbDir = new File(webAppRoot, "db");
    dbDir.mkdir();
    File logDir = new File(webAppRoot, "log");
    logDir.mkdir();

    writeMessage(out, "Extracting application template ...");
    try {
      Checksum checksum = JarUtil.extract(new JarFile("./lib/snipsnap-template.war", true), appDir);
      checksum.store(new File(webAppRoot, "CHECKSUMS"));
    } catch (IOException e) {
      System.err.println("Installer: error while extracting default template: " + e);
      errors.put("fatal", "Unable to extract default application, please see server.log for details!");
      sendError(session, errors, request, response);
      return;
    }

    // store configuration in thread, for database creation
    Application app = Application.getInstance(session);
    System.out.println("app: " + app);
    app.setConfiguration(config);

    writeMessage(out, "Saving local configuration ...");
    config.setFile(new File(webAppRoot.getAbsoluteFile(), "application.conf"));

    writeMessage(out, "Creating database ...");
    boolean useMcKoi = request.getParameter("usemckoi") != null ? true : false;
    String jdbcURL = request.getParameter("jdbc");
    String jdbcDrv = request.getParameter("driver");
    if (useMcKoi || jdbcURL == null || jdbcURL.length() == 0) {
      File dbConfFile = new File(webAppRoot, "db.conf");
      jdbcURL = MckoiEmbeddedJDBCDriver.MCKOI_PREFIX + dbConfFile.getAbsolutePath();
      jdbcDrv = "org.snipsnap.util.MckoiEmbeddedJDBCDriver";

      config.setJDBCURL(jdbcURL + "?create=true");
      config.setJDBCDriver(jdbcDrv);

      try {
        Properties dbConf = new Properties();
        dbConf.load(new FileInputStream("./conf/db.conf"));
        dbConf.store(new FileOutputStream(dbConfFile), "SnipSnap Database configuration: " + config.getName());

        CreateDB.createDB(config);
      } catch (IOException e) {
        System.err.println("Installer: error creating database: " + e);
        errors.put("fatal", "Unable to create database!");
        sendError(session, errors, request, response);
        return;
      }
      config.setJDBCURL(jdbcURL);
      writeMessage(out, "Inserting inital data into database ...");
      CreateDB.insertData(config);

    } else {
      config.setJDBCURL(jdbcURL);
      config.setJDBCDriver(jdbcDrv);
      CreateDB.createDB(config);
      CreateDB.insertData(config);
    }

    config.setLogger("org.snipsnap.util.log.NullLogger");
    config.store();

    writeMessage(out, "Starting application ...");
    try {
      ApplicationLoader.loadApplication(config);
    } catch (Exception e) {
      System.err.println("Installer: unable to start application: " + e);
      e.printStackTrace();
      errors.put("fatal", "Cannot start application: " + e);
      sendError(session, errors, request, response);
      return;
    }


    if (serverConfig.getAdminLogin() == null && serverConfig.getAdminPassword() == null) {
      System.out.println("Installer: Creating System Installer Account using " + config.getAdminLogin());
      serverConfig.setAdminLogin(config.getAdminLogin());
      serverConfig.setAdminPassword(config.getAdminPassword());
      serverConfig.setAdminEmail(config.getAdminEmail());
      serverConfig.store();
    }

    writeMessage(out, "Installation finished.");
    session.removeAttribute("config");
    String url = config.getUrl();
    System.out.println("Redirecting to "+url);
    response.sendRedirect(url);
  }

  private void sendError(HttpSession session, Map errors, HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    session.setAttribute("errors", errors);
    response.sendRedirect(SnipLink.absoluteLink(request, "/exec/install.jsp"));
  }

  private void writeMessage(PrintWriter out, String message) {
    out.println(message);
    out.flush();
  }
}
