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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

public class ConfigurationMap {
  Properties properties = null;
  File configFile = null;

  public ConfigurationMap() {
    Properties defaults = new Properties();
    try {
      defaults.load(Configuration.class.getResourceAsStream("/org/snipsnap/config/defaults.conf"));
    } catch (Exception e) {
      System.err.println("Configuration: unable to load defaults: " + e.getMessage());
    }

    // instantiate properties with defaults
    properties = new Properties(defaults);
  }

  public ConfigurationMap(File file) throws IOException {
    this();
    load(file);
  }

  public ConfigurationMap(String filePath) throws IOException {
    this(new File(filePath));
  }

  public File getConfDir() {
    if(configFile != null) {
      return configFile.getParentFile();
    }
    return null;
  }

  /**
   * Change the file to store the configuration in.
   * @param file the new file
   */
  public void setFile(File file) {
    configFile = file;
  }

  public File getFile() {
    return configFile;
  }

  public void load(File file) throws IOException {
    setFile(file);
    load();
  }

  public void load() throws IOException {
    if(configFile != null) {
      properties.load(new FileInputStream(configFile));
    }
  }

  public void store() throws IOException {
    if(configFile != null) {
      properties.store(new FileOutputStream(configFile), "SnipSnap configuration $Revision$");
    }
  }

  public void set(String name, String value) {
    properties.setProperty(name, value);
  }

  public String get(String name) {
    String value = replaceTokens(properties.getProperty(name));
    return "".equals(value) ? null : value;
  }

  private String replaceTokens(String value) {
    int idx = value.indexOf("${CONFDIR}");
    if(idx != -1) {
      StringBuffer replaced = new StringBuffer();
      if(idx > 0) {
        replaced.append(value.substring(0, idx));
      }
      replaced.append(getConfDir().getPath());
      int endIdx = idx + "${CONFDIR}".length();
      if(endIdx < value.length()) {
        replaced.append(value.substring(idx + endIdx));
      }
      return replaced.toString();
    }
    return value;
  }

  public String get(String name, String defaultValue) {
    String value = get(name);
    if(value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Create a real locale object from the strings in our configuration.
   * @return the locale configured
   */
  public Locale getLocale() {
    String language = get(Configuration.APP_LANGUAGE, "en");
    String country = get(Configuration.APP_COUNTRY, "US");
    return new Locale(language, country);
  }

  /**
   * Return base url to Snipsnap instance
   */
  public String getUrl() {
    String host = get(Configuration.APP_REAL_HOST);
    String port = get(Configuration.APP_REAL_PORT);
    String path = get(Configuration.APP_REAL_PATH);

    if (host == null) {
      host = get(Configuration.APP_HOST);
      port = get(Configuration.APP_PORT);
      path = get(Configuration.APP_PATH);
    }

    StringBuffer tmp = new StringBuffer();
    tmp.append("http://");
    try {
      tmp.append(host == null || host.length() == 0 ? InetAddress.getLocalHost().getHostName() : host);
    } catch (UnknownHostException e) {
      tmp.append(System.getProperty("host", "localhost"));
    }
    if (!"80".equals(port)) {
      tmp.append(":");
      tmp.append(port);
    }
    tmp.append(path != null ? path : "");
    return tmp.toString();
  }

  /**
   * Returns an external URL to this instance of SnipSnap
   *
   * @param target Path to add to url, e.g. "/exec/"
   */
  public String getUrl(String target) {
    return getUrl() + target;
  }

  /**
   * Returns an external URL of a snip
   * @param snipName Name of the snip
   */
  public String getSnipUrl(String snipName) {
    return getUrl("/space/" + snipName);
  }

  // PERMISSIONS
  public boolean allow(String action) {
    return "allow".equals(get(action));
  }

  public boolean deny(String action) {
    return "deny".equals(get(action));
  }

  public boolean getAllowRegister() {
    return !deny(Configuration.APP_PERM_REGISTER);
  }

  public boolean isInstalled() {
    return get(Configuration.APP_JDBC_URL) != null;
  }

  public String getVersion() {
    String version = System.getProperty(ServerConfiguration.VERSION);
    if (null == version) {
      version = get(ServerConfiguration.VERSION);
    }
    return version;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    Iterator it = properties.keySet().iterator();
    result.append("{");
    while(it.hasNext()) {
      String key = (String)it.next();
      result.append(key).append("="+properties.get(key)).append(",");
    }
    result.append("}");
    return result.toString();
  }
}
