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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

/**
 * Implementation of a configuration map which is used as storage for configuration parameters.
 * @see Configuration
 */
public class ConfigurationMap {

  private final static String DEFAULTS_CONF = "/org/snipsnap/config/defaults.conf";
  private final static String TRANSPOSE_MAP = "/org/snipsnap/config/transpose.map";

  private Properties properties = null;
  private Properties defaults = null;
  private File configFile = null;

  // internal transposition map for old property file versions
  private Properties transposeMap = null;

  public ConfigurationMap() {
    defaults = new Properties();
    try {
      defaults.load(Configuration.class.getResourceAsStream(DEFAULTS_CONF));
    } catch (Exception e) {
      System.err.println("Configuration: unable to load defaults: " + e.getMessage());
    }

    // instantiate properties with defaults
    properties = new Properties(defaults);

    try {
      transposeMap = new Properties();
      transposeMap.load(Configuration.class.getResourceAsStream(TRANSPOSE_MAP));
    } catch (Exception e) {
      System.err.println("Configuration: unable to load transposition map: " + e.getMessage());
    }
  }

  public ConfigurationMap(File file) throws IOException {
    this();
    load(file);
  }

  public ConfigurationMap(String filePath) throws IOException {
    this(new File(filePath));
  }

  /**
   * Returns the configuration directory which is identical to the parent directory of the
   * configuration file or null.
   * @return the configuration directory
   */
  public File getConfDir() {
    if (configFile != null) {
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

  /**
   * The currently set file where the configuration was loaded or will be stored.
   * @return the configuration file
   */
  public File getFile() {
    return configFile;
  }

  /**
   * Loads the configuration from a specific file overriding any previously set file.
   * @param file the file to load from
   * @throws IOException
   */
  public void load(File file) throws IOException {
    setFile(file);
    load();
  }

  /**
   * Loads the configuration from a previously set file.
   * @throws IOException
   */
  public void load() throws IOException {
    if (configFile != null) {
      properties.load(new FileInputStream(configFile));
      if (convertOldProperties()) {
        System.err.println("Configuration: storing converted configuration file to " + getFile());
        store();
      }
    }
  }

  private boolean convertOldProperties() {
    boolean hasChanged = false;
    Iterator propIt = transposeMap.keySet().iterator();
    while (propIt.hasNext()) {
      String oldProperty = (String) propIt.next();
      String newProperty = transposeMap.getProperty(oldProperty);
      String value = properties.getProperty(oldProperty);
      if (value != null) {
        if (newProperty != null) {
          if (newProperty.startsWith("@DEPRECATED")) {
            if (convertDeprecatedProperty(oldProperty, value)) {
              properties.remove(oldProperty);
              hasChanged = true;
            } else {
              System.out.println("INFO: Configuration option '" + oldProperty + "' is deprecated:");
              System.out.println("INFO: " + newProperty.substring("@DEPRECATED".length()));
              System.out.println("INFO: Please edit configuration file manually.");
            }
          }
          if (newProperty.startsWith("@DUPLICATE")) {
            newProperty = newProperty.substring("@DUPLICATE".length());
            String newValue = properties.getProperty(newProperty);
            if (null == newValue || "".equals(newValue)) {
              System.out.println("INFO: duplicating value of '" + oldProperty + "' to '" + newProperty + "'");
              properties.setProperty(newProperty, value);
              hasChanged = true;
            }
          } else {
            System.out.println("INFO: converting '" + oldProperty + "' to '" + newProperty + "'");
            properties.remove(oldProperty);
            properties.setProperty(newProperty, value);
            hasChanged = true;
          }
        }
      }
    }
    return hasChanged;
  }

  private boolean convertDeprecatedProperty(String oldProperty, String value) {
    if ("app.domain".equals(oldProperty) || "app.url".equals(oldProperty)) {
      if (value != null && value.length() > 0) {
        try {
          URL url = new URL(value);
          System.out.println("INFO: converting '" + oldProperty + "' to 'app.real.*'");
          properties.setProperty(Configuration.APP_REAL_HOST, url.getHost());
          if (url.getPort() >= 0 && url.getPort() != 80) {
            properties.setProperty(Configuration.APP_REAL_PORT, "" + url.getPort());
          }
          properties.setProperty(Configuration.APP_REAL_PATH, url.getPath());
        } catch (MalformedURLException e) {
          System.out.println("WARNING: unable to convert '" + oldProperty + "': malformed URL: '" + value + "'");
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Stores the configuration in a properties file if a file has been set with setFile() or
   * if the configuration has been loaded from a file. An existing file is overwritten.
   * @throws IOException
   */
  public void store() throws IOException {
    if (configFile != null) {
      properties.store(new FileOutputStream(configFile), "SnipSnap configuration $Revision$");
    }
  }

  /**
   * Set configuration parameter.
   * @param name the configuration parameter name as in Configuration interface
   * @see Configuration
   * @param value the new value
   */
  public void set(String name, String value) {
    properties.setProperty(name, value);
  }

  /**
   * Get configuration parameter. This method ensures that a parameter that is empty
   * (null or null sized string) is returned as NULL.
   * @param name the configuration parameter name as in Configuration interface
   * @see Configuration
   * @return the value of the configuration parameter
   */
  public String get(String name) {
    String value = replaceTokens(properties.getProperty(name));
    return "".equals(value) ? null : value;
  }

  // TODO replace with generic replacement method
  private String replaceTokens(String value) {
    if(value != null) {
      int idx = value.indexOf("${CONFDIR}");
      if (idx != -1) {
        StringBuffer replaced = new StringBuffer();
        if (idx > 0) {
          replaced.append(value.substring(0, idx));
        }
        replaced.append(getConfDir().getPath());
        int endIdx = idx + "${CONFDIR}".length();
        if (endIdx < value.length()) {
          replaced.append(value.substring(endIdx));
        }
        return replaced.toString();
      }
    }
    
    return value;
  }

  public String get(String name, String defaultValue) {
    String value = get(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  public Properties getProperties() {
    return properties;
  }

  public String getDefault(String name) {
    String value = defaults.getProperty(name);
    return ("".equals(value) ? null : value);
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

  public String getPath() {
    if ("true".equals(get(Configuration.APP_REAL_AUTODETECT))) {
      String realPath = get(Configuration.APP_REAL_PATH);
      if (null != realPath) {
        return realPath;
      }
    }
    return get(Configuration.APP_PATH);
  }

  /**
   * Return base url to Snipsnap instance
   */
  public String getUrl() {
    String host = get(Configuration.APP_REAL_HOST);
    String port = get(Configuration.APP_REAL_PORT);
    String path = get(Configuration.APP_REAL_PATH);

    if (null == host) {
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
    if (port != null && !"80".equals(port)) {
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
    if (getFile().exists() && new File(getConfDir(), "db").exists()) {
      return true;
    }
    return false;
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
    while (it.hasNext()) {
      String key = (String) it.next();
      result.append(key).append("=" + properties.get(key)).append(",");
    }
    result.append("}");
    return result.toString();
  }
}
