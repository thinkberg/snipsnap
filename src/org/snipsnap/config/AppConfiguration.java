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

import org.snipsnap.snip.SnipLink;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * AppConfiguration Object that contains local installation information.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AppConfiguration extends Configuration {

  private final static String APP_NAME = "app.name";
  private final static String APP_HOST = "app.host";
  private final static String APP_PORT = "app.port";
  private final static String APP_PATH = "app.path";
  private final static String APP_PERM = "app.perm";
  private final static String APP_TAGLINE = "app.tagline";
  private final static String APP_LOGGER = "app.logger";
  private final static String APP_JDBC_URL = "app.jdbc.url";
  private final static String APP_JDBC_DRIVER = "app.jdbc.driver";

  protected static AppConfiguration instance;

  public static AppConfiguration getInstance() {
    if (null == instance) {
      instance = new AppConfiguration();
    }
    return instance;
  }

  public static AppConfiguration getInstance(String file) throws IOException {
    return getInstance(new File(file));
  }

  public static AppConfiguration getInstance(File file) throws IOException {
    if (null == instance) {
      instance = new AppConfiguration(file);
    }

    return instance;
  }

  public AppConfiguration() {
    super();
  }

  public AppConfiguration(File file) throws IOException {
    super(file);
  }

  public void setName(String name) {
    setProperty(AppConfiguration.APP_NAME, name);
  }

  public String getName() {
    return getProperty(AppConfiguration.APP_NAME);
  }

  // HOST CONFIGURATION
  public void setHost(String host) {
    setProperty(AppConfiguration.APP_HOST, host);
  }

  public String getHost() {
    String host = getProperty(AppConfiguration.APP_HOST);
    return host != null && host.length() == 0 ? null : host;
  }

  public void setPort(int port) {
    setProperty(AppConfiguration.APP_PORT, "" + port);
  }

  public int getPort() {
    try {
      return Integer.parseInt(getProperty(AppConfiguration.APP_PORT));
    } catch (NumberFormatException e) {
      return 8668;
    }
  }

  public void setContextPath(String contextPath) {
    setProperty(AppConfiguration.APP_PATH, contextPath);
  }

  public String getContextPath() {
    return getProperty(AppConfiguration.APP_PATH);
  }

  public String getUrl() {
    return getUrl("");
  }

  public String getUrl(String target) {
    StringBuffer url = new StringBuffer();
    url.append("http://");
    try {
      url.append(getHost() == null ? InetAddress.getLocalHost().getHostName() : getHost());
    } catch (UnknownHostException e) {
      url.append(System.getProperty("host", "localhost"));
    }
    int port = getPort();
    if (port != 80) {
      url.append(":");
      url.append(port);
    }
    url.append(getContextPath());
    url.append(target);
    return url.toString();
  }

  public String getSnipUrl(String snipName) {
    return getUrl("space/"+snipName);
  }

  public void setTagLine(String tagline) {
    setProperty(AppConfiguration.APP_TAGLINE, tagline);
  }

  public String getTagLine() {
    String tagline = getProperty(APP_TAGLINE);
    return tagline != null ? tagline : "The easy Weblog and Wiki Software.";
  }

  public void setJDBCURL(String url) {
    setProperty(AppConfiguration.APP_JDBC_URL, url);
  }

  public String getJDBCURL() {
    return getProperty(AppConfiguration.APP_JDBC_URL);
  }

  public void setJDBCDriver(String driver) {
    setProperty(AppConfiguration.APP_JDBC_DRIVER, driver);
  }

  public String getJDBCDriver() {
    return getProperty(AppConfiguration.APP_JDBC_DRIVER);
  }

  public void setLogger(String logger) {
    setProperty(AppConfiguration.APP_LOGGER, logger);
  }

  public String getLogger() {
    return getProperty(AppConfiguration.APP_LOGGER);
  }

  public boolean allow(String action) {
    return "allow".equals(getProperty(AppConfiguration.APP_PERM+action));
  }

  public boolean deny(String action) {
    return "deny".equals(getProperty(AppConfiguration.APP_PERM+action));
  }

  public boolean allowExternalImages() {
    return allow("externalImages");
  }
}
