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

import org.snipsnap.app.Application;
import org.snipsnap.app.ApplicationManager;
import org.snipsnap.app.ApplicationStorage;
import org.snipsnap.container.Components;
import org.snipsnap.snip.HomePage;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.XMLSnipImport;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.RenderEngineLabel;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.UserManagerFactory;

import java.io.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class InitializeDatabase {

  private static ThreadLocal output = new ThreadLocal();

  private static void message(String message) {
    if (null != output.get()) {
      PrintWriter writer = (PrintWriter) output.get();
      writer.println("[" + Application.get().getConfiguration().getName() + "] " + message);
      writer.flush();
    }
  }

  public static String init(Configuration config, Writer w) throws Exception {
    output.set(new PrintWriter(w));

    Application app = Application.get();
    app.setConfiguration(config);

    ApplicationManager appManager = (ApplicationManager) Components.getComponent(ApplicationManager.class);
    Collection prefixes = appManager.getPrefixes();

    if (prefixes != null && prefixes.contains(config.getPrefix())) {
      throw new Exception("the prefix " + config.getPrefix() + " already exists");
    }

    Properties prefixProps = appManager.createApplication(config.getName(), config.getPrefix());
    String appOid = prefixProps.getProperty(ApplicationStorage.OID);
    try {
      message("created application oid: " + appOid);
      app.storeObject(Application.OID, appOid);

      new File(config.getFileStore()).mkdirs();
      // automatically created by the indexer
      // (new File(config.getIndexPath())).mkdirs();

      // get an instance of the snip space
      SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);

      createAdministrator(config);

      // disable notifications and pings before loading snips and posting
      String ping = config.get(Configuration.APP_PERM_WEBLOGSPING);
      String notify = config.get(Configuration.APP_PERM_NOTIFICATION);
      config.set(Configuration.APP_PERM_WEBLOGSPING, "deny");
      config.set(Configuration.APP_PERM_NOTIFICATION, "deny");

      // load defaults
      InputStream data = getLocalizedResource("i18n.snipsnap", "snip", config.getLocale());
      XMLSnipImport.load(data, XMLSnipImport.OVERWRITE | XMLSnipImport.IMPORT_USERS | XMLSnipImport.IMPORT_SNIPS);


      message("loading defaults into configuration space");
      // load other configurations
      createConfigSnipFromFile(Configuration.SNIPSNAP_CONFIG_API, "/defaults/apidocs.txt", space);
      createConfigSnipFromFile(Configuration.SNIPSNAP_CONFIG_ASIN, "/defaults/asinservices.txt", space);
      createConfigSnipFromFile(Configuration.SNIPSNAP_CONFIG_BOOK, "/defaults/bookservices.txt", space);
      createConfigSnipFromFile(Configuration.SNIPSNAP_CONFIG_PING, "/defaults/weblogsping.txt", space);
      createConfigSnipFromFile(Configuration.SNIPSNAP_CONFIG_ROBOTS, "/defaults/robotdetect.txt", space);
      createConfigSnipFromFile(Configuration.SNIPSNAP_CONFIG_ROBOTS_TXT, "/defaults/robots.txt", space);
      createConfigSnipFromFile(Configuration.SNIPSNAP_CONFIG_WIKI, "/defaults/intermap.txt", space);

      File themeTemplateDir = new File(config.getWebInfDir(), "themes");
      XMLSnipImport.load(new FileInputStream(new File(themeTemplateDir, "SnipSnap-Theme-" + config.getTheme() + ".snip")),
                         XMLSnipImport.OVERWRITE | XMLSnipImport.IMPORT_SNIPS);

      postFirstBlog(config, space);

      config.set(Configuration.APP_PERM_WEBLOGSPING, ping);
      config.set(Configuration.APP_PERM_NOTIFICATION, notify);

      // last, but not least store to file and configuration snip
      storeConfiguration(config, space);

      ConfigurationManager configManager = ConfigurationManager.getInstance();
      configManager.addConfiguration(appOid, config);
    } catch (Exception e) {
      appManager.removeApplication(appOid);
      config.getFileStore(appOid).delete();
      e.printStackTrace();
      throw e;
    }

    return appOid;
  }

  public static void createConfigSnipFromFile(String name, String file, SnipSpace space) throws IOException {
    String content = getResourceAsString(InitializeDatabase.class.getResourceAsStream(file));
    createConfigSnip(name, content, space);
  }

  public static Snip createConfigSnip(String name, String content, SnipSpace space) {
    Snip snip = space.create(name, content);
    snip.getPermissions().add(Permissions.EDIT_SNIP, Roles.ADMIN);
    snip.getPermissions().add(Permissions.ATTACH_TO_SNIP, Roles.ADMIN);
    snip.getLabels().addLabel(new RenderEngineLabel("RenderEngine", "org.snipsnap.render.PlainTextRenderEngine"));
    space.systemStore(snip);
    return snip;
  }

  private static void postFirstBlog(Configuration config, SnipSpace space) throws IOException {
    message("posting initial weblog entry");
    String weblogPost = getResourceAsString(getLocalizedResource("i18n.welcome", "blog", config.getLocale()));
    String title = weblogPost.substring(0, weblogPost.indexOf('\n'));
    weblogPost = weblogPost.substring(weblogPost.indexOf('\n') + 1);
    space.getBlog().post(weblogPost, title);
  }

  public final static String LOGO_FILE = "_logofile";
  public final static String LOGO_FILE_TYPE = "_logofiletype";

  private static void storeConfiguration(Configuration config, SnipSpace space) throws IOException {
    String logoFileName = config.get(LOGO_FILE);
    String logoFileType = config.get(LOGO_FILE_TYPE);
    config.getProperties().remove(LOGO_FILE);
    config.getProperties().remove(LOGO_FILE_TYPE);

    message("creating configuration snip '" + Configuration.SNIPSNAP_CONFIG + "'");
    config.setConfigured("true");
    ByteArrayOutputStream configStream = new ByteArrayOutputStream();
    config.store(configStream);
    Snip configSnip = createConfigSnip(Configuration.SNIPSNAP_CONFIG,
                                       new String(configStream.toString("UTF-8")),
                                       space);
    String logo = config.getLogo();
    if (logoFileName != null && !"".equals(logoFileName)) {
      File logoFile = new File(logoFileName);
      File relativeFileLocation = new File(new File(Configuration.SNIPSNAP_CONFIG), logo);
      new File(config.getFilePath(), Configuration.SNIPSNAP_CONFIG).mkdirs();
      File attFile = new File(config.getFilePath(), relativeFileLocation.getPath());
      logoFile.renameTo(attFile);
      configSnip.getAttachments().addAttachment(logo, logoFileType, attFile.length(),
                                                relativeFileLocation.getPath());
      space.store(configSnip);
    }
  }

  private static User createAdministrator(Configuration config) {
    // create admin account
    message("creating administrator account and snip");
    UserManager um = UserManagerFactory.getInstance();
    User admin = um.load(config.getAdminLogin());
    if (admin != null) {
      message("overriding administrator: " + admin);
      um.remove(admin);
    }
    admin = um.create(config.getAdminLogin(), config.getAdminPassword(), config.getAdminEmail());
    admin.getRoles().add(Roles.ADMIN);
    admin.getRoles().add(Roles.EDITOR);
    um.store(admin);

    // make sure the encrypted password is stored
    config.setAdminPassword(admin.getPasswd());

    // set current user and create it's homepage
    Application.get().setUser(admin);
    HomePage.create(config.getAdminLogin());

    return admin;
  }


  private static String getResourceAsString(InputStream is) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(is));
    StringBuffer contents = new StringBuffer();
    String line = null;
    while ((line = in.readLine()) != null) {
      contents.append(line).append("\n");
    }
    return contents.toString();
  }

  /**
   * Get the input stream for a localized resource file given the resource base
   * name and its extension and locale.
   * @param resource the base name of the resource
   * @param ext the file name extension (like .snip, .blog)
   * @param locale the locale currently used
   * @return the input stream of the resource or null if none was found
   */
  private static InputStream getLocalizedResource(String resource, String ext, Locale locale) {
    InputStream is = findLocalizedResource(resource, ext, locale);
    if (is == null) {
      is = getResource(resource, null, null, null, ext);
    }
    return is;
  }

  /**
   * Method to find a certain resource based on locale information.
   * @param base the base name of the resource
   * @param ext extension of the resource appended as ".ext"
   * @return the resource found or null
   */
  private static InputStream findLocalizedResource(String base, String ext, Locale locale) {
    String language = locale.getLanguage();
    String country = locale.getCountry();
    String variant = locale.getVariant();

    InputStream is = null;
    if ((is = getResource(base, language, country, variant, ext)) != null) {
      return is;
    } else if ((is = getResource(base, language, country, null, ext)) != null) {
      return is;
    } else if ((is = getResource(base, language, null, null, ext)) != null) {
      return is;
    }
    return getResource(base, "en", null, null, ext);
  }

  /**
   * Loads a resource from the CLASSPATH by appending language, country, variant to the base name.
   * Example: messages_en_US.snip or messages_en.snip
   * @param base the base name of the file
   * @param ext extension appended as ".ext" to the resource name
   * @return an input stream of the resource or null
   */
  private static InputStream getResource(String base, String language, String country, String variant, String ext) {
    String file = "/" + base.replace('.', '/') +
      (language != null ? "_" + language : "") +
      (country != null ? "_" + country : "") +
      (variant != null ? "_" + variant : "") +
      "." + ext;
    return InitializeDatabase.class.getResourceAsStream(file);
  }
}
