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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * AppConfiguration Object that contains local installation information.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AppConfiguration extends Configuration {

  public final static String APP_ADMIN_PASSWORD = "admin.password";
  public final static String APP_ADMIN_LOGIN = "admin.login";
  public final static String APP_ADMIN_EMAIL = "admin.email";
  public final static String APP_CACHE = "app.cache";
  public final static String APP_WEBLOG_DATE_FORMAT = "app.weblogDateFormat";
  public final static String APP_TIMEZONE = "app.timezone";
  public final static String APP_NAME = "app.name";
  public final static String APP_HOST = "app.host";
  public final static String APP_PORT = "app.port";
  public final static String APP_PATH = "app.path";
  public final static String APP_URL = "app.url";
  public final static String APP_THEME = "app.theme";
  public final static String APP_PERM = "app.perm";
  public final static String APP_DOMAIN = "app.domain";
  public final static String APP_MAILHOST = "app.mail.host";
  public final static String APP_MAILDOMAIN = "app.mail.domain";
  public final static String APP_MAILBLOGPASSWD = "app.mailblog.password";
  public final static String APP_POP3HOST = "app.pop3.host";
  public final static String APP_POP3USER = "app.pop3.user";
  public final static String APP_POP3PASSWD = "app.pop3.password";
  public final static String APP_TAGLINE = "app.tagline";
  public final static String APP_LOGO = "app.logo";
  public final static String APP_LOGGER = "app.logger";
  public final static String APP_ENCODING = "app.encoding";
  public final static String APP_LOCALE = "app.locale";
  public final static String APP_JDBC_URL = "app.jdbc.url";
  public final static String APP_JDBC_DRIVER = "app.jdbc.driver";
  public final static String APP_INDEX_PATH = "app.index.path";
  public final static String APP_FILE_PATH = "app.file.path";
  public final static String APP_COORDINATES = "app.geoCoordinates";

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

  public void setAdminLogin(String login) {
    setProperty(APP_ADMIN_LOGIN, login);
  }

  public String getAdminLogin() {
    return getProperty(APP_ADMIN_LOGIN);
  }

  public void setAdminPassword(String password) {
    setProperty(APP_ADMIN_PASSWORD, password);
  }

  public String getAdminPassword() {
    return getProperty(APP_ADMIN_PASSWORD);
  }

  public void setAdminEmail(String email) {
    setProperty(APP_ADMIN_EMAIL, email);
  }

  public String getAdminEmail() {
    return getProperty(APP_ADMIN_EMAIL);
  }

  public void setName(String name) {
    setProperty(AppConfiguration.APP_NAME, name);
  }

  public String getName() {
    return getProperty(AppConfiguration.APP_NAME);
  }

  public String getLocaleString() {
    String locale = getProperty(AppConfiguration.APP_LOCALE);
    return locale == null ? "en" : locale;
  }

  public String getCountry() {
    return "us";
  }

  public Locale getLocale() {
    return new Locale(getLocaleString(), getCountry());
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

  /**
   *  set the base URL of the application including
   */
  public void setUrl(String url) {
    setProperty(AppConfiguration.APP_URL, url);
  }

  /**
   * Return base url to Snipsnap instance
   */
  public String getUrl() {
    String url = getProperty(AppConfiguration.APP_URL);
    if (null == url) {
      url = getProperty(AppConfiguration.APP_DOMAIN);
      if (url != null) {
        setProperty(AppConfiguration.APP_URL, url);
        try {
          store();
        } catch (IOException e) {
          System.out.println("WARNING: "+getName()+": unable to store configuration. Edit application.conf and change app.domain to app.url");
          e.printStackTrace();
        }
      }
    }
    if (null == url || url.length() == 0) {
      StringBuffer tmp = new StringBuffer();
      tmp.append("http://");
      try {
        tmp.append(getHost() == null ? InetAddress.getLocalHost().getHostName() : getHost());
      } catch (UnknownHostException e) {
        tmp.append(System.getProperty("host", "localhost"));
      }
      int port = getPort();
      if (port != 80) {
        tmp.append(":");
        tmp.append(port);
      }
      tmp.append(getContextPath());
      url = tmp.toString();
    }
    return url;
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
   *
   * @param snipName Name of the snip
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

  public void setMailHost(String mailhost) {
    setProperty(APP_MAILHOST, mailhost);
  }

  public String getMailHost() {
    return getProperty(APP_MAILHOST);
  }

  public void setMailDomain(String maildomain) {
    setProperty(APP_MAILDOMAIN, maildomain);
  }

  public String getMailDomain() {
    return getProperty(APP_MAILDOMAIN);
  }

  public String getMailBlogPassword() {
    return getProperty(APP_MAILBLOGPASSWD);
  }

  public String getPop3Password() {
    return getProperty(APP_POP3PASSWD);
  }

  public String getPop3User() {
    return getProperty(APP_POP3USER);
  }

  public String getPop3Host() {
    return getProperty(APP_POP3HOST);
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

  public String getCache() {
    return getProperty(AppConfiguration.APP_CACHE);
  }

  public void setLogger(String logger) {
    setProperty(AppConfiguration.APP_LOGGER, logger);
  }

  public String getLogger() {
    return getProperty(AppConfiguration.APP_LOGGER);
  }

  public String getWeblogDateFormatString() {
    String format = getProperty(AppConfiguration.APP_WEBLOG_DATE_FORMAT);
    return format == null ? "EEEE, dd. MMMM yyyy" : format;
  }

  public SimpleDateFormat getWeblogDateFormat() {
    return new SimpleDateFormat(getWeblogDateFormatString(), getLocale());
  }

  public String getTimeZone() {
    String timezone = getProperty(AppConfiguration.APP_TIMEZONE);
    return timezone == null ? "+1.00" : timezone;
  }

  public String getEncoding() {
    String encoding = getProperty(AppConfiguration.APP_ENCODING);
    return encoding == null ? "UTF-8" : encoding;
  }

  public String getCoordinates() {
    return getProperty(AppConfiguration.APP_COORDINATES);
  }

  public String getFileStorePath() {
    String path = getProperty(AppConfiguration.APP_FILE_PATH);
    return path == null ? getFile().getParent() + "/files" : path;
  }

  public String getIndexPath() {
    String path = getProperty(AppConfiguration.APP_INDEX_PATH);
    return path == null ? getFile().getParent() + "/index" : path;
  }

  // PERMISSIONS
  public boolean allow(String action) {
    return "allow".equals(getProperty(AppConfiguration.APP_PERM + "." + action));
  }

  public boolean deny(String action) {
    return "deny".equals(getProperty(AppConfiguration.APP_PERM + "." + action));
  }

  public boolean allowExternalImages() {
    return allow(AppConfiguration.PERM_EXTERNAL_IMAGES);
  }

  public boolean isInstalled() {
    return getJDBCURL() != null;
  }
}
