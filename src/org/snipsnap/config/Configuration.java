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
package org.snipsnap.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * A configuration object. Contains information about server and admin login.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Configuration {

  private Properties properties;

  public final static String INIT_PARAM = "config";

  public final static String SERVER_VERSION = "server.version";
  public final static String SERVER_WEBAPP_ROOT = "server.webapp.root";
  public final static String SERVER_ADMIN_PORT = "server.admin.port";
  public final static String SERVER_ENCODING = "server.encoding";


  public final static String ADMIN_LOGIN = "admin.login";
  public final static String ADMIN_PASSWORD = "admin.password";
  public final static String ADMIN_EMAIL = "admin.email";

  private File file = null;

  /**
   * Create an instance of configuration, unconfigured
   */
  public Configuration() {
    properties = new Properties();
  }

  /**
   * Create an instance of configuration from a file given as string.
   * @param file the config file to load
   * @throws IOException
   */
  public Configuration(String file) throws IOException {
    this(new File(file));
  }

  /**
   * Create an instance of configuration from a file.
   * @param file the config file to load
   * @throws IOException
   */
  public Configuration(File file) throws IOException {
    this();
    this.file = file;
    properties.load(new FileInputStream(file));
  }


  public Configuration(Configuration config) {
    properties = (Properties)config.properties.clone();
  }

  /**
   * Change the file to store the configuration in.
   * @param file the new file
   */
  public void setFile(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  /**
   * Store configuration in the file it was loaded from.
   * @throws IOException
   */
  public void store() throws IOException {
    if (file != null) {
      store(file);
    } else {
      throw new IOException("no configuration file known, use store(File file)");
    }
  }

  /**
   * Store configuration file explicitely in a specifified file.
   * @param configFile the file to store in
   * @throws IOException
   */
  public void store(File configFile) throws IOException {
    setFile(configFile);
    FileOutputStream out = new FileOutputStream(file);
    store(out);
    out.close();
  }

  /**
   * Store using an output stream. This is used to override defaults in Properties and adds
   * a header.
   * @param out the output stream
   * @throws IOException
   */
  public void store(OutputStream out) throws IOException {
    properties.store(out, "SnipSnap configuration $Revision$");
  }

  public void setAdminLogin(String login) {
    properties.setProperty(ADMIN_LOGIN, login);
  }

  public String getAdminLogin() {
    return properties.getProperty(ADMIN_LOGIN);
  }

  public void setAdminPassword(String password) {
    properties.setProperty(ADMIN_PASSWORD, password);
  }

  public String getAdminPassword() {
    return properties.getProperty(ADMIN_PASSWORD);
  }


  public void setAdminEmail(String email) {
    properties.setProperty(ADMIN_EMAIL, email);
  }

  public String getAdminEmail() {
    return properties.getProperty(ADMIN_EMAIL);
  }

  public String getVersion() {
    String version = getProperty(SERVER_VERSION);
    if (null == version) {
      version = System.getProperty("snipsnap." + SERVER_VERSION);
    }
    return version;
  }

  public void setProperty(String name, String value) {
    properties.setProperty(name, value);
  }

  public String getProperty(String name) {
    return properties.getProperty(name);
  }

  public String toString() {
    return properties.toString();
  }
}
