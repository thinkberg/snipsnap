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
package org.snipsnap.server;

import org.mortbay.jetty.servlet.WebApplicationContext;
import org.snipsnap.config.Globals;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.xmlrpc.AuthXmlRpcHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.prefs.Preferences;

public class AdminXmlRpcHandler extends AuthXmlRpcHandler {
  public AdminXmlRpcHandler() {
    super();
  }

  protected boolean authenticate(String user, String password) {
    Preferences serverPrefs = Preferences.userNodeForPackage(ServerConfiguration.class);
    String adminPassword = (String) serverPrefs.get(ServerConfiguration.ADMIN_PASS, null);
    return null != adminPassword && adminPassword.equals(password);
  }

  public Hashtable getApplications() {
    Hashtable appList = new Hashtable();
    Iterator appIt = ApplicationLoader.applications.keySet().iterator();
    while (appIt.hasNext()) {
      String appName = (String) appIt.next();
      WebApplicationContext context = (WebApplicationContext) ApplicationLoader.applications.get(appName);
      String[] hosts = context.getHosts();
      if (hosts == null) {
        hosts = new String[1];
        try {
          hosts[0] = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
          hosts[0] = "localhost";
        }
      }
      int port = context.getHttpServer().getListeners()[0].getPort();
      String url = "http://" + hosts[0] + (port != 80 ? ":" + port : "") + context.getContextPath();
      appList.put(appName, url);
    }
    return appList;
  }

  public String shutdown() {
    System.out.println("INFO: received remote shutdown request (waiting 1s) ...");
    new Thread() {
      public synchronized void run() {
        System.err.println("AdminXmlRpcHandler: shutdown waiting for 1s ...");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          System.err.println("AdminXmlRpcHandler: shutdown delay cancelled");
        }
        System.exit(0);
      }
    }.start();
    return "SnipSnap Server is shutting down ...";
  }

  public String install(String name, String host, String port, String path) throws Exception {
    //System.err.println("AdminXmlRpcHandler: install("+name+","+port+","+path+")");
    Preferences serverPrefs = Preferences.userNodeForPackage(ServerConfiguration.class);
    File root = new File(serverPrefs.get(ServerConfiguration.WEBAPP_ROOT, System.getProperty("user.home")));
    File webAppDir = new File(root, name + "/webapp");
    File webInf = new File(webAppDir, "WEB-INF");
    webInf.mkdirs();

    File applicationConf = new File(webInf, "application.conf");
    if (!applicationConf.exists()) {
      Properties installConfig = new Properties();
      installConfig.setProperty(Globals.APP_HOST, host);
      installConfig.setProperty(Globals.APP_PORT, port);
      installConfig.setProperty(Globals.APP_PATH, path);
      try {
        installConfig.store(new FileOutputStream(applicationConf), " Bootstrap Configuration");
        ApplicationLoader.loadApplication(root.getPath(), name);
        installConfig.load(new FileInputStream(applicationConf));
        return ApplicationLoader.getUrl(installConfig) + "?key=" + installConfig.getProperty(Globals.APP_INSTALL_KEY);
      } catch (Exception e) {
        applicationConf.delete();
        e.printStackTrace();
        throw e;
      }
    } else {
      throw new Exception("'" + applicationConf.getPath() + "' exists, delete application first");
    }
  }

  /**
   * Remove a web application from the server. Handle with care, because this deletes
   * all the data you might have stored in that web application including snips and
   * attachments.
   *
   * @param name
   * @return
   */
  public Boolean delete(String name, Boolean backup) throws Exception {
    //System.err.println("AdminXmlRpcHandler: delete(" + name+")");
    Preferences serverPrefs = Preferences.userNodeForPackage(ServerConfiguration.class);
    File root = new File(serverPrefs.get(ServerConfiguration.WEBAPP_ROOT, System.getProperty("user.home")));
    File app = new File(root, name);
    if (app.exists()) {
      try {
        ApplicationLoader.unloadApplication(root.getPath(), name);
      } catch (Exception e) {
        System.err.println("AdminXmlRpcHandler: unload failed: " + e);
      }
      if (backup.booleanValue()) {
        createBackupJar(name + ".backup.jar", app);
      }
      return new Boolean(app.delete());
    }
    return Boolean.TRUE;
  }

  private void createBackupJar(String jarName, File file) throws IOException {
    JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarName),
                                              new Manifest());
    System.err.println("Jar: created '" + jarName + "'");
    try {
      addToJarFile(jar, file);
    } finally {
      jar.close();
    }
  }

  private void addToJarFile(JarOutputStream jar, File file) throws IOException {
    JarEntry entry = new JarEntry(file.getPath());
    jar.putNextEntry(entry);
    if (file.isDirectory()) {
      File[] fileList = file.listFiles();
      for (int fileNo = 0; fileNo < fileList.length; fileNo++) {
        addToJarFile(jar, fileList[fileNo]);
      }
    } else {
      FileInputStream fileStream = new FileInputStream(file);
      try {
        byte buffer[] = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileStream.read(buffer)) != -1) {
          jar.write(buffer, 0, bytesRead);
        }
        System.err.println("Jar: added '" + file.getPath() + "'");
      } catch (IOException e) {
        System.err.println("Jar: error adding '" + file.getPath() + "': " + e);
      } finally {
        fileStream.close();
      }
    }
  }
}
