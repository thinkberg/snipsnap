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
package org.snipsnap.net.admin;

import org.mortbay.util.InetAddrPort;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.CreateDB;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.util.Checksum;
import org.snipsnap.util.JarUtil;
import org.snipsnap.util.MckoiEmbeddedJDBCDriver;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class InstallServlet extends HttpServlet {

  protected final static String ATT_APPLICATION = "app";
  protected final static String ATT_CONFIG = "config";

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession();
    Application app = Application.getInstance(session);
    Configuration config = app.getConfiguration();

    if(!config.isInstalled()) {
      session.setAttribute(ATT_APPLICATION, app);
      session.setAttribute(ATT_CONFIG, config);

      if(request.getParameter("install") == null) {
        if(!request.getContextPath().equals(config.getPath())) {
          config.set(Configuration.APP_PATH, request.getContextPath());
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/install/install.jsp");
        dispatcher.forward(request, response);
        return;
      } else {
        Map params = request.getParameterMap();
        Iterator iterator = params.keySet().iterator();
        Map paramMap = new HashMap();
        while (iterator.hasNext()) {
          String key = (String) iterator.next();
          String[] values = (String[]) params.get(key);
          paramMap.put(key, values[0]);
        }

        Map errors = install(paramMap, config, getServletContext().getRealPath("/"));
        if(null == errors || errors.isEmpty()) {
          RequestDispatcher dispatcher = request.getRequestDispatcher("/install/install.jsp");
          dispatcher.forward(request, response);
        }
      }
    }
    response.sendRedirect(SnipLink.absoluteLink("/"));
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  private Map install(Map params, Configuration config, String root) {
    File webAppRoot = new File(root);
    Map errors = new HashMap();

    // change this to response.getWriter() to enable display on webpage
    PrintWriter out = new PrintWriter(System.out);
    writeMessage(out, "Installing SnipSnap ...");

    String tagline = (String)params.get("tagline");
    config.set(Configuration.APP_TAGLINE, tagline != null && tagline.length() > 0 ? tagline : "The easy Weblog and Wiki Software.");

    String logo = (String)params.get("logoimage");
    if (logo != null && logo.length() > 0) {
      config.set(Configuration.APP_LOGO, logo);
    }

    config.set(Configuration.APP_LOGGER, "org.radeox.util.logging.NullLogger");
    config.set(Configuration.APP_CACHE, "full");
    config.set(Configuration.APP_TIMEZONE, "+1.00");
    config.set(Configuration.APP_WEBLOGDATEFORMAT, "EEEE, dd. MMMM yyyy");

    if ((String)params.get("weblogsPing") != null) {
      config.set(Configuration.APP_PERM_WEBLOGSPING, "allow");
    } else {
      config.set(Configuration.APP_PERM_WEBLOGSPING, "deny");
    }

    // set application name ...
    writeMessage(out, "Checking application name ...");
    String appName = (String)params.get("appname");
    if (null == appName || appName.length() == 0) {
      System.err.println("Installer: application name too short");
      errors.put("appname", "An application name is required!");
    }
    config.set(Configuration.APP_NAME, appName != null ? appName : "");

    // set user name and email, check that information
    writeMessage(out, "Checking user name and password ...");
    String adminName = (String)params.get("username");
    if (!checkUserName(adminName)) {
      System.err.println("Installer: user name too short: "+adminName);
      errors.put("login", "You must enter a user name with at least 3 characters!");
    } else {
      config.set(Configuration.APP_ADMIN_LOGIN, adminName);
    }
    config.set(Configuration.APP_ADMIN_EMAIL, (String)params.get("email"));

    String password = (String)params.get("password");
    String password2 = (String)params.get("password2");
    if (!checkPassword(password, password2)) {
      System.err.println("Installer: passwords do not match");
      errors.put("password", "Passwords do not match! Passwords have to be at least 6 characters.");
    } else {
      config.set(Configuration.APP_ADMIN_PASSWORD, password);
    }

//    writeMessage(out, "Checking server host and port information ...");
//    // set host name and port if provided
//    config.set(Configuration.APP_HOST, (String)params.get("host"));
//    try {
//      // the parsing is a test to make sure that this is an integer
//      config.set(Configuration.APP_PORT, "" + Integer.parseInt((String)params.get("port")));
//    } catch (NumberFormatException e) {
//      System.err.println("Installer: port '" + (String)params.get("port") + "' is not a valid number");
//      errors.put("port", "The port '" + (String)params.get("port") + "' is not a valid number!");
//    }
//    String context = (String)params.get("context");
//    if (context != null) {
//      if (context.length() > 1 && !context.startsWith("/")) {
//        context = "/" + context;
//      }
//      if (context.endsWith("/")) {
//        context = context.substring(0, context.length() - 2);
//      }
//      config.set(Configuration.APP_PATH, context);
//    }

    if (errors.size() != 0) {
      return errors;
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
      return errors;
    }

    String autoUrl = (String)params.get("autoUrl");
    config.set(Configuration.APP_REAL_AUTODETECT, autoUrl != null ? "true" : "false");

    String realUrl = (String)params.get("realurl");
    if(null != realUrl) {
      try {
        URL url = new URL(realUrl);
        config.set(Configuration.APP_REAL_HOST, url.getHost());
        config.set(Configuration.APP_REAL_PORT, ""+url.getPort());
        config.set(Configuration.APP_REAL_PATH, url.getPath());
      } catch (MalformedURLException e) {
        errors.put("realurl", "The real URL you entered is not correct: "+e.getMessage());
        return errors;
      }
    }

    String mailhost = (String)params.get("mailhost");
    if (mailhost != null) {
      config.set(Configuration.APP_MAIL_HOST, mailhost);
    } else {
      writeMessage(out, "No mail host defined, we will try to find one or you will not be able to mail.");
    }

    String maildomain = (String)params.get("maildomain");
    if (maildomain != null) {
      config.set(Configuration.APP_MAIL_DOMAIN, maildomain);
    }



    File webInf = new File(webAppRoot, "WEB-INF");
    File dbDir = new File(webInf, "db");
    dbDir.mkdir();
    File logDir = new File(webInf, "log");
    logDir.mkdir();

    String theme = (String)params.get("theme");
    if (theme == null || theme.length() == 0) {
      errors.put("theme", "Please select a theme to install.");
      return errors;
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
        return errors;
      }
    }

    // store configuration in thread, for database creation
    Application app = Application.get();
    app.setConfiguration(config);

    writeMessage(out, "Saving local configuration ...");
    try {
      config.store();
    } catch (IOException e) {
      errors.put("fatal", "Unable to save configuration: "+config.getFile());
      return errors;
    }

    writeMessage(out, "Creating database ...");
    boolean useMcKoi = (String)params.get("usemckoi") != null ? true : false;
    String jdbcURL = (String)params.get("jdbc");
    String jdbcDrv = (String)params.get("driver");
    if (useMcKoi || jdbcURL == null || jdbcURL.length() == 0) {
      useMcKoi = true;
      File dbConfFile = new File(webInf, "db.conf");
      MckoiEmbeddedJDBCDriver.register();
      jdbcURL = MckoiEmbeddedJDBCDriver.MCKOI_PREFIX + dbConfFile.getPath();
      jdbcDrv = "org.snipsnap.util.MckoiEmbeddedJDBCDriver";

      // the first time create the database
      config.set(Configuration.APP_JDBC_URL, jdbcURL + "?create=true");
      config.set(Configuration.APP_JDBC_DRIVER, jdbcDrv);

      CreateDB.createDB(config);
      config.set(Configuration.APP_JDBC_URL, jdbcURL);
    } else {
      config.set(Configuration.APP_JDBC_URL, jdbcURL);
      config.set(Configuration.APP_JDBC_DRIVER, jdbcDrv);
      CreateDB.createDB(config);
    }

    writeMessage(out, "Inserting inital data into database ...");
    CreateDB.createAdmin(config);
    try {
      CreateDB.insertData(config, new FileInputStream(new File(webInf, "defaults.snip")));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      errors.put("fatal", "Unable to insert default database content.");
      return errors;
    }
    CreateDB.postFirst(config);

    writeMessage(out, "Adding additional data from theme " + theme);
    File themeSnip = new File(webInf, theme + ".snip");
    if (themeSnip.exists()) {
      try {
        CreateDB.insertData(config, new FileInputStream(new File(webInf, theme + ".snip")));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        errors.put("warn", "Unable to insert theme content.");
      }
    }
    if (useMcKoi) {
      try {
        MckoiEmbeddedJDBCDriver.deregister();
      } catch (SQLException e) {
        System.err.println("Installer: error deregistering jdbc driver: " + e);
      }
    }

    try {
      config.store();
    } catch (IOException e) {
      e.printStackTrace();
      errors.put("fatal", "Unable to store configuration.");
      return errors;
    }

    writeMessage(out, "Installation finished.");

    return errors;
  }

  private void writeMessage(PrintWriter out, String message) {
    out.println(message);
    out.flush();
  }

  public static boolean checkPassword(String password, String password2) {
    return (null != password && password.length() > 5
      && password.equals(password2));
  }

  public static boolean checkUserName(String login) {
    return (null != login && login.length() >= 3);
  }

}