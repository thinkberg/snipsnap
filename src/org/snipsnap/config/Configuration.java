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

import java.util.Locale;
import java.util.Properties;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Interface template for easy usage of the configuration.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public interface Configuration {
  public final static String SNIPSNAP_CONFIG = "SnipSnap/config";
  public final static String SNIPSNAP_CONFIG_API = "SnipSnap/config/apidocs";
  public final static String SNIPSNAP_CONFIG_WIKI = "SnipSnap/config/interwiki";
  public final static String SNIPSNAP_CONFIG_PING = "SnipSnap/config/weblogsping";
  public final static String SNIPSNAP_CONFIG_ASIN = "SnipSnap/config/asinservices";
  public final static String SNIPSNAP_CONFIG_BOOK = "SnipSnap/config/bookservices";
  public final static String SNIPSNAP_CONFIG_ROBOTS = "SnipSnap/config/robots";
  public final static String SNIPSNAP_CONFIG_ROBOTS_TXT = "SnipSnap/config/robots.txt";
  public final static String SNIPSNAP_THEMES = "SnipSnap/themes";

  public String get(String name);
  public void set(String name, String value);

  public void setWebInfDir(File file);
  public File getWebInfDir();

  public void load(InputStream stream) throws IOException;
  public void store(OutputStream stream) throws IOException;
  public void storeBootstrap(OutputStream stream) throws IOException;
  
  // get all properties
  public Properties getProperties();
  public String getDefault(String name);

  // LOCALE
  Locale getLocale();

  // URL HANDLING
  String getUrl();
  String getUrl(String target);
  String getSnipUrl(String snipName);

  // PERMISSIONS
  boolean allow(String action);
  boolean deny(String action);

  // TODO remove/replace
  boolean getAllowRegister();

  // GENERICS
  public String getVersion();
  boolean isInstalled();

  // EASY ACCESS
  
  // automatically created interface/constants stub from
  // src/org/snipsnap/config/defaults.conf
  // generated on 9/29/03 4:40 PM
  // constant/getter for 'app.admin.email'
  public final static String APP_ADMIN_EMAIL = "app.admin.email";
  public String getAdminEmail();
  public String setAdminEmail(String value);
  // constant/getter for 'app.admin.login'
  public final static String APP_ADMIN_LOGIN = "app.admin.login";
  public String getAdminLogin();
  public String setAdminLogin(String value);
  // constant/getter for 'app.admin.password'
  public final static String APP_ADMIN_PASSWORD = "app.admin.password";
  public String getAdminPassword();
  public String setAdminPassword(String value);
  // constant/getter for 'app.cache'
  public final static String APP_CACHE = "app.cache";
  public String getCache();
  public String setCache(String value);
  // constant/getter for 'app.country'
  public final static String APP_COUNTRY = "app.country";
  public String getCountry();
  public String setCountry(String value);
  // constant/getter for 'app.encodedSpace'
  public final static String APP_ENCODEDSPACE = "app.encodedSpace";
  public String getEncodedSpace();
  public String setEncodedSpace(String value);
  // constant/getter for 'app.encoding'
  public final static String APP_ENCODING = "app.encoding";
  public String getEncoding();
  public String setEncoding(String value);
  // constant/getter for 'app.file.path'
  public final static String APP_FILE_PATH = "app.file.path";
  public String getFilePath();
  public String setFilePath(String value);
  // constant/getter for 'app.geoCoordinates'
  public final static String APP_GEOCOORDINATES = "app.geoCoordinates";
  public String getGeoCoordinates();
  public String setGeoCoordinates(String value);
  // constant/getter for 'app.host'
  public final static String APP_HOST = "app.host";
  public String getHost();
  public String setHost(String value);
  // constant/getter for 'app.index.path'
  public final static String APP_INDEX_PATH = "app.index.path";
  public String getIndexPath();
  public String setIndexPath(String value);
  // constant/getter for 'app.installed'
  public final static String APP_INSTALLED = "app.installed";
  public String getInstalled();
  public String setInstalled(String value);
  // constant/getter for 'app.jdbc.driver'
  public final static String APP_JDBC_DRIVER = "app.jdbc.driver";
  public String getJdbcDriver();
  public String setJdbcDriver(String value);
  // constant/getter for 'app.jdbc.password'
  public final static String APP_JDBC_PASSWORD = "app.jdbc.password";
  public String getJdbcPassword();
  public String setJdbcPassword(String value);
  // constant/getter for 'app.jdbc.url'
  public final static String APP_JDBC_URL = "app.jdbc.url";
  public String getJdbcUrl();
  public String setJdbcUrl(String value);
  // constant/getter for 'app.jdbc.user'
  public final static String APP_JDBC_USER = "app.jdbc.user";
  public String getJdbcUser();
  public String setJdbcUser(String value);
  // constant/getter for 'app.language'
  public final static String APP_LANGUAGE = "app.language";
  public String getLanguage();
  public String setLanguage(String value);
  // constant/getter for 'app.logger'
  public final static String APP_LOGGER = "app.logger";
  public String getLogger();
  public String setLogger(String value);
  // constant/getter for 'app.logo'
  public final static String APP_LOGO = "app.logo";
  public String getLogo();
  public String setLogo(String value);
  // constant/getter for 'app.mail.blog.password'
  public final static String APP_MAIL_BLOG_PASSWORD = "app.mail.blog.password";
  public String getMailBlogPassword();
  public String setMailBlogPassword(String value);
  // constant/getter for 'app.mail.domain'
  public final static String APP_MAIL_DOMAIN = "app.mail.domain";
  public String getMailDomain();
  public String setMailDomain(String value);
  // constant/getter for 'app.mail.host'
  public final static String APP_MAIL_HOST = "app.mail.host";
  public String getMailHost();
  public String setMailHost(String value);
  // constant/getter for 'app.mail.pop3.host'
  public final static String APP_MAIL_POP3_HOST = "app.mail.pop3.host";
  public String getMailPop3Host();
  public String setMailPop3Host(String value);
  // constant/getter for 'app.mail.pop3.interval'
  public final static String APP_MAIL_POP3_INTERVAL = "app.mail.pop3.interval";
  public String getMailPop3Interval();
  public String setMailPop3Interval(String value);
  // constant/getter for 'app.mail.pop3.password'
  public final static String APP_MAIL_POP3_PASSWORD = "app.mail.pop3.password";
  public String getMailPop3Password();
  public String setMailPop3Password(String value);
  // constant/getter for 'app.mail.pop3.user'
  public final static String APP_MAIL_POP3_USER = "app.mail.pop3.user";
  public String getMailPop3User();
  public String setMailPop3User(String value);
  // constant/getter for 'app.name'
  public final static String APP_NAME = "app.name";
  public String getName();
  public String setName(String value);
  // constant/getter for 'app.path'
  public final static String APP_PATH = "app.path";
  public String getPath();
  public String setPath(String value);
  // constant/getter for 'app.perm.createSnip'
  public final static String APP_PERM_CREATESNIP = "app.perm.createSnip";
  public String getPermCreateSnip();
  public String setPermCreateSnip(String value);
  // constant/getter for 'app.perm.externalImages'
  public final static String APP_PERM_EXTERNALIMAGES = "app.perm.externalImages";
  public String getPermExternalImages();
  public String setPermExternalImages(String value);
  // constant/getter for 'app.perm.multiplePosts'
  public final static String APP_PERM_MULTIPLEPOSTS = "app.perm.multiplePosts";
  public String getPermMultiplePosts();
  public String setPermMultiplePosts(String value);
  // constant/getter for 'app.perm.notification'
  public final static String APP_PERM_NOTIFICATION = "app.perm.notification";
  public String getPermNotification();
  public String setPermNotification(String value);
  // constant/getter for 'app.perm.register'
  public final static String APP_PERM_REGISTER = "app.perm.register";
  public String getPermRegister();
  public String setPermRegister(String value);
  // constant/getter for 'app.perm.weblogsPing'
  public final static String APP_PERM_WEBLOGSPING = "app.perm.weblogsPing";
  public String getPermWeblogsPing();
  public String setPermWeblogsPing(String value);
  // constant/getter for 'app.port'
  public final static String APP_PORT = "app.port";
  public String getPort();
  public String setPort(String value);
  // constant/getter for 'app.real.autodetect'
  public final static String APP_REAL_AUTODETECT = "app.real.autodetect";
  public String getRealAutodetect();
  public String setRealAutodetect(String value);
  // constant/getter for 'app.real.host'
  public final static String APP_REAL_HOST = "app.real.host";
  public String getRealHost();
  public String setRealHost(String value);
  // constant/getter for 'app.real.path'
  public final static String APP_REAL_PATH = "app.real.path";
  public String getRealPath();
  public String setRealPath(String value);
  // constant/getter for 'app.real.port'
  public final static String APP_REAL_PORT = "app.real.port";
  public String getRealPort();
  public String setRealPort(String value);
  // constant/getter for 'app.start.snip'
  public final static String APP_START_SNIP = "app.start.snip";
  public String getStartSnip();
  public String setStartSnip(String value);
  // constant/getter for 'app.tagline'
  public final static String APP_TAGLINE = "app.tagline";
  public String getTagline();
  public String setTagline(String value);
  // constant/getter for 'app.theme'
  public final static String APP_THEME = "app.theme";
  public String getTheme();
  public String setTheme(String value);
  // constant/getter for 'app.timezone'
  public final static String APP_TIMEZONE = "app.timezone";
  public String getTimezone();
  public String setTimezone(String value);
  // constant/getter for 'app.weblogDateFormat'
  public final static String APP_WEBLOGDATEFORMAT = "app.weblogDateFormat";
  public String getWeblogDateFormat();
  public String setWeblogDateFormat(String value);

}
