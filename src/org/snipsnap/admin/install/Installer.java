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

import org.mortbay.util.InetAddrPort;
import org.snipsnap.admin.util.CommandHandler;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.CreateDB;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.server.ApplicationLoader;
import org.snipsnap.server.AdminServer;
import org.snipsnap.user.User;
import org.snipsnap.util.Checksum;
import org.snipsnap.util.JarUtil;
import org.snipsnap.util.MckoiEmbeddedJDBCDriver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
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
      response.sendRedirect(request.getContextPath() + "/");
      return;
    }
    Map errors = new HashMap();
    session.removeAttribute("errors");

    // get config from session
    ServerConfiguration serverConfig = (ServerConfiguration) session.getAttribute(CommandHandler.ATT_CONFIG);
    User admin = (User) session.getAttribute(CommandHandler.ATT_ADMIN);
    if (null == serverConfig || (serverConfig.getAdminLogin() != null && admin == null)) {
      response.sendRedirect(request.getContextPath() + "/");
      return;
    }

    // create a new configuration object
    Configuration config = ConfigurationProxy.newInstance();
    session.setAttribute("config", config);

    // change this to response.getWriter() to enable display on webpage
    PrintWriter out = new PrintWriter(System.out);
    writeMessage(out, "Installing SnipSnap ...");

    String tagline = request.getParameter("tagline");
    config.set(Configuration.APP_TAGLINE, tagline != null && tagline.length() > 0 ? tagline : "The easy Weblog and Wiki Software.");

    String logo = request.getParameter("logoimage");
    if (logo != null && logo.length() > 0) {
      config.set(Configuration.APP_LOGO, logo);
    }

    config.set(Configuration.APP_LOGGER, "org.radeox.util.logging.NullLogger");
    config.set(Configuration.APP_CACHE, "full");
    config.set(Configuration.APP_TIMEZONE, "+1.00");
    config.set(Configuration.APP_WEBLOGDATEFORMAT, "EEEE, dd. MMMM yyyy");

    if(request.getParameter("weblogsPing") != null) {
      config.set(Configuration.APP_PERM_WEBLOGSPING, "allow");
    } else {
      config.set(Configuration.APP_PERM_WEBLOGSPING, "deny");
    }

    // set application name ...
    writeMessage(out, "Checking application name ...");
    String appName = request.getParameter("appname");
    if (null == appName || appName.length() == 0) {
      System.err.println("Installer: application name too short");
      errors.put("appname", "An application name is required!");
    }
    config.set(Configuration.APP_NAME, appName != null ? appName : "");

    // set user name and email, check that information
    writeMessage(out, "Checking user name and password ...");
    String adminName = request.getParameter("username");
    if (!checkUserName(adminName)) {
      System.err.println("Installer: user name too short");
      errors.put("login", "You must enter a user name with at least 3 characters!");
    } else {
      config.set(Configuration.APP_ADMIN_LOGIN, adminName);
    }
    config.set(Configuration.APP_ADMIN_EMAIL, request.getParameter("email"));

    String password = request.getParameter("password");
    String password2 = request.getParameter("password2");
    if (!checkPassword(password, password2)) {
      System.err.println("Installer: passwords do not match");
      errors.put("password", "Passwords do not match! Passwords have to be at least 6 characters.");
    } else {
      config.set(Configuration.APP_ADMIN_PASSWORD, password);
    }

    writeMessage(out, "Checking server host and port information ...");
    // set host name and port if provided
    config.set(Configuration.APP_HOST, request.getParameter("host"));
    try {
      // the parsing is a test to make sure that this is an integer
      config.set(Configuration.APP_PORT, ""+Integer.parseInt(request.getParameter("port")));
    } catch (NumberFormatException e) {
      System.err.println("Installer: port '" + request.getParameter("port") + "' is not a valid number");
      errors.put("port", "The port '" + request.getParameter("port") + "' is not a valid number!");
    }
    String context = request.getParameter("context");
    if (context != null) {
      if (context.length() > 1 && !context.startsWith("/")) {
        context = "/" + context;
      }
      if (context.endsWith("/")) {
        context = context.substring(0, context.length() - 2);
      }
      config.set(Configuration.APP_PATH, context);
    }

    if (errors.size() != 0) {
      sendError(session, errors, request, response);
      return;
    }

    InetAddrPort addrPort = new InetAddrPort();
    try {
      String host = config.get(Configuration.APP_HOST);
      if (host != null && host.length() > 0) {
        addrPort.setHost(host);
      } else {
        addrPort.setHost(InetAddrPort.__0_0_0_0);
      }
      addrPort.setPort(Integer.parseInt(config.get(Configuration.APP_PORT)));
    } catch (Exception e) {
      System.err.println("Installer: error binding host name: " + e);
      errors.put("host", "The host you entered is unknown, leave blank to bind server to the default host name.");
      sendError(session, errors, request, response);
      return;
    }

    String autoUrl = request.getParameter("autoUrl");
    config.set(Configuration.APP_REAL_AUTODETECT, autoUrl != null ? "true" : "false");

    // TODO handle installation jsp file correctly
    String realHost = request.getParameter("realhost");
    if (realHost != null) {
      config.set(Configuration.APP_REAL_HOST, realHost);
    }
    String realPort = request.getParameter("realport");
    if (realPort != null) {
      config.set(Configuration.APP_REAL_PORT, realPort);
    }
    String realPath = request.getParameter("realpath");
    if (realPath != null) {
      config.set(Configuration.APP_REAL_PATH, realPath);
    }

    String mailhost = request.getParameter("mailhost");
    if (mailhost != null) {
      config.set(Configuration.APP_MAIL_HOST, mailhost);
    } else {
      writeMessage(out, "No mail host defined, we will try to find one or you will not be able to mail.");
    }

    String maildomain = request.getParameter("maildomain");
    if (maildomain != null) {
      config.set(Configuration.APP_MAIL_DOMAIN, maildomain);
    }

    // create application root directory
    File webAppRoot = new File(serverConfig.getProperty(ServerConfiguration.WEBAPP_ROOT) +
                               "/" + normalize(config.get(Configuration.APP_NAME)));
    writeMessage(out, "Creating web application directories ...");
    if (!webAppRoot.mkdirs()) {
      System.err.println("Installer: error creating applications root directory");
      errors.put("fatal", "Unable to create applications root directory: " + webAppRoot);
      sendError(session, errors, request, response);
      return;
    }

    File webInf = new File(webAppRoot, "WEB-INF");
    webInf.mkdir();
    File dbDir = new File(webInf, "db");
    dbDir.mkdir();
    File logDir = new File(webInf, "log");
    logDir.mkdir();

    writeMessage(out, "Extracting application template ...");
    try {
      Checksum checksum = JarUtil.extract(new JarFile("lib/snipsnap-template.war", true), webAppRoot);
      checksum.store(new File(webInf, "CHECKSUMS"));
    } catch (IOException e) {
      System.err.println("Installer: error while extracting default template: " + e);
      errors.put("fatal", "Unable to extract default application, please see server.log for details!");
      sendError(session, errors, request, response);
      return;
    }

    String theme = request.getParameter("theme");
    if (theme == null || theme.length() == 0) {
      errors.put("theme", "Please select a theme to install.");
      sendError(session, errors, request, response);
    }
    if (theme != null && theme.length() != 0) {
      config.set(Configuration.APP_THEME, "/" + theme);
      writeMessage(out, "Extracting theme ...");
      try {
        JarFile themeJar = new JarFile("lib/snipsnap-theme-" + theme + ".jar", true);
        Checksum checksum = JarUtil.checksumJar(themeJar);
        Set files = checksum.getFileNames();
        List install = new ArrayList();
        Iterator it = files.iterator();
        while (it.hasNext()) {
          String name = (String) it.next();
          if (name != null && (name.startsWith("css/") || name.startsWith("images/"))) {
            install.add(name);
          }
        }
        JarUtil.extract(themeJar, webAppRoot, install, null);
        install = Arrays.asList(new Object[]{theme + ".snip"});
        JarUtil.extract(themeJar, webInf, install, null);
        checksum.store(new File(webInf, "CHECKSUMS.theme"));
      } catch (IOException e) {
        System.err.println("Installer: error while extracting theme: " + e);
        errors.put("fatal", "Unable to extract selected theme, please see server.log for details!");
        sendError(session, errors, request, response);
        return;
      }
    }

    // store configuration in thread, for database creation
    Application app = Application.getInstance(session);
    app.setConfiguration(config);

    writeMessage(out, "Saving local configuration ...");
    File appConfFile = new File(webInf.getAbsoluteFile(), "application.conf");
    config.store(new FileOutputStream(appConfFile));

    writeMessage(out, "Creating database ...");
    boolean useMcKoi = request.getParameter("usemckoi") != null ? true : false;
    String jdbcURL = request.getParameter("jdbc");
    String jdbcDrv = request.getParameter("driver");
    if (useMcKoi || jdbcURL == null || jdbcURL.length() == 0) {
      useMcKoi = true;
      File dbConfFile = new File(webInf, "db.conf");
      MckoiEmbeddedJDBCDriver.register();
      jdbcURL = MckoiEmbeddedJDBCDriver.MCKOI_PREFIX + dbConfFile.getPath();
      jdbcDrv = "org.snipsnap.util.MckoiEmbeddedJDBCDriver";

      config.set(Configuration.APP_JDBC_URL, jdbcURL + "?create=true");
      config.set(Configuration.APP_JDBC_DRIVER, jdbcDrv);

      try {
        Properties dbConf = new Properties();
        dbConf.load(new FileInputStream("conf/db.conf"));
        dbConf.store(new FileOutputStream(dbConfFile), "SnipSnap Database configuration: " + config.get(Configuration.APP_NAME));

        CreateDB.createDB(config);
      } catch (IOException e) {
        System.err.println("Installer: error creating database: " + e);
        errors.put("fatal", "Unable to create database!");
        sendError(session, errors, request, response);
        return;
      }
      config.set(Configuration.APP_JDBC_URL, jdbcURL);
    } else {
      config.set(Configuration.APP_JDBC_URL, jdbcURL);
      config.set(Configuration.APP_JDBC_DRIVER, jdbcDrv);
      CreateDB.createDB(config);
    }

    writeMessage(out, "Inserting inital data into database ...");
    CreateDB.createAdmin(config);
    CreateDB.insertData(config, new FileInputStream("conf/snipsnap.snip"));
    CreateDB.postFirst(config);
    File themeSnip = new File(webInf, theme + ".snip");
    if (themeSnip.exists()) {
      writeMessage(out, "Adding additional data from theme " + theme);
      CreateDB.insertData(config, new FileInputStream(themeSnip));
    }
    if (useMcKoi) {
      try {
        MckoiEmbeddedJDBCDriver.deregister();
      } catch (SQLException e) {
        System.err.println("Installer: error deregistering jdbc driver: " + e);
      }
    }

    writeMessage(out, "Starting application ...");
    if (!AdminServer.execute(Integer.parseInt(serverConfig.getProperty(ServerConfiguration.ADMIN_PORT)), "start", config.getName())) {
      System.out.println("Cannot execute administrative command: 'start " + config.getName() + "'");
      errors.put("fatal", "Unable to start application '"+config.getName()+"'");
      sendError(session, errors, request, response);
      return;
    }

    if (serverConfig.getAdminLogin() == null && serverConfig.getAdminPassword() == null) {
      System.out.println("Installer: Creating System Installer Account using " + config.get(Configuration.APP_ADMIN_LOGIN));
      serverConfig.setAdminLogin(config.get(Configuration.APP_ADMIN_LOGIN));
      serverConfig.setAdminPassword(config.get(Configuration.APP_ADMIN_PASSWORD));
      serverConfig.setAdminEmail(config.get(Configuration.APP_ADMIN_EMAIL));
      serverConfig.store();
    }

    writeMessage(out, "Installation finished.");
    session.removeAttribute("config");

    // redirect to url
    String url = config.getUrl();
    System.out.println("Redirecting to " + url);
    response.sendRedirect(url);
  }

  private void sendError(HttpSession session, Map errors, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    session.setAttribute("errors", errors);
    response.sendRedirect(request.getContextPath() + "/exec/install.jsp");
  }

  private void writeMessage(PrintWriter out, String message) {
    out.println(message);
    out.flush();
  }

  private String normalize(String name) {
    return name.replace(' ', '_');
  }

  public static boolean checkPassword(String password, String password2) {
    return (null != password && password.length() > 5
        && password.equals(password2));
  }

  public static boolean checkUserName(String login) {
    return (null != login && login.length() >= 3);
  }
}
