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

  public final static String APP_NAME = "app.name";
  public final static String APP_HOST = "app.host";
  public final static String APP_PORT = "app.port";
  public final static String APP_PATH = "app.path";
  public final static String APP_PERM = "app.perm";
  public final static String APP_DOMAIN = "app.domain";
  public final static String APP_TAGLINE = "app.tagline";
  public final static String APP_LOGO = "app.logo";
  public final static String APP_LOGGER = "app.logger";
  public final static String APP_JDBC_URL = "app.jdbc.url";
  public final static String APP_JDBC_DRIVER = "app.jdbc.driver";


  public final static String PERM_NOTIFICATION = "notification";
  public final static String PERM_WEBLOGS_PING = "weblogsPing";
  public final static String PERM_EXTERNAL_IMAGES = "externalImages";

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

  public String getLocale() {
    return "en";
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

  public void setDomain(String domain) {
    setProperty(AppConfiguration.APP_DOMAIN, domain);
  }

  public String getDomain() {
    return getProperty(AppConfiguration.APP_DOMAIN);
  }

  /**
   * Return root url to Snipsnap instance
   */
  public String getUrl() {
    StringBuffer url = new StringBuffer();
    String domain = getProperty(AppConfiguration.APP_DOMAIN);
    if (domain != null && domain.length() > 0) {
      url.append(domain);
    } else {
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
    }
    return url.toString();
  }

  /**
   * Returns an external URL to this instance of SnipSnap
   *
   * @target Path to add to url, e.g. "/exec/"
   */
  public String getUrl(String target) {
    return getUrl() + target;
  }


  /**
   * Returns an external URL of a snip
   *
   * @snipName Name of the snip
   */
  public String getSnipUrl(String snipName) {
    return getUrl("/space/" + snipName);
  }

  public void setTagLine(String tagline) {
    setProperty(AppConfiguration.APP_TAGLINE, tagline);
  }

  public String getTagLine() {
    return getProperty(APP_TAGLINE);
  }

  public void setLogoImage(String image) {
    setProperty(APP_LOGO, image);
  }

  public String getLogoImage() {
    return getProperty(APP_LOGO);
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
    return "allow".equals(getProperty(AppConfiguration.APP_PERM + "." + action));
  }

  public boolean deny(String action) {
    return "deny".equals(getProperty(AppConfiguration.APP_PERM + "." + action));
  }

  public boolean allowExternalImages() {
    return allow(AppConfiguration.PERM_EXTERNAL_IMAGES);
  }
}
