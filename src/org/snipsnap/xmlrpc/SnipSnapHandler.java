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

package org.snipsnap.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.snipsnap.app.Application;
import org.snipsnap.app.ApplicationManager;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationManager;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.Globals;
import org.snipsnap.config.InitializeDatabase;
import org.snipsnap.container.Components;
import org.snipsnap.snip.XMLSnipExport;
import org.snipsnap.snip.XMLSnipImport;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.Links;
import org.snipsnap.snip.storage.SnipSerializer;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.render.filter.links.BackLinks;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Handles XML-RPC calls for the SnipSnap API
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipSnapHandler extends AuthXmlRpcHandler implements XmlRpcHandler {
  // @TODO use Gabriel instead
  private final static List FREE_METHODS = Arrays.asList(new String[]{
    "getVersion",
    "authenticateUser"
  });

  private final static List PREFIX_METHODS = Arrays.asList(new String[]{
    "getSnip",
    "createSnip",
    "removeSnip",
    "getSnipAsXml",
    "dumpXml",
    "restoreXml",
  });

  public static final String API_PREFIX = "snipSnap";

  private SnipSpace space;
  private AuthenticationService authenticationService;
  private UserManager um;
  private ApplicationManager applicationManager;

  public SnipSnapHandler(AuthenticationService authenticationService,
                         SnipSpace space,
                         UserManager manager,
                         ApplicationManager applicationManager) {
    this.authenticationService = authenticationService;
    this.um = manager;
    this.space = space;
    this.applicationManager = applicationManager;
  }

  protected boolean authenticate(String username, String password) {
    Globals globals = ConfigurationProxy.getInstance();
    if(password != null && password.equals(globals.getInstallKey())) {
      return true;
    }

    User user = authenticationService.authenticate(username, password);
    if (user != null && user.isAdmin()) {
      Application.get().setUser(user);
      return true;
    }
    System.err.println("XML-RPC authenticate: invalid login for " + username);
    return false;
  }

  public Object execute(String method, Vector vector, String user, String password) throws Exception {
    if (FREE_METHODS.contains(method)) {
      return super.execute(method, vector);
    } else if (PREFIX_METHODS.contains(method)) {
      if(!(vector.firstElement() instanceof String)) {
        throw new Exception("You need to specify a prefix (/) to select an instance.");
      }
      String prefix = (String) vector.firstElement();
      String appOid = applicationManager.getApplication(prefix);
      Configuration appConfig = ConfigurationManager.getInstance().getConfiguration(appOid);
      if (appConfig != null) {
        if(prefix.equals(vector.get(0))) {
          vector.remove(0);
        }
        Application.get().setConfiguration(appConfig);
        Application.get().storeObject(Application.OID, appOid);
        return super.execute(method, vector, user, password);
      }
      throw new Exception("no instance found for prefix '" + prefix + "'");
    }
    return super.execute(method, vector, user, password);
  }

  public String getName() {
    return API_PREFIX;
  }

  public String getSnipAsXml(String name) {
    Snip snip = space.load(name);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    OutputFormat outputFormat = OutputFormat.createCompactFormat();
    outputFormat.setEncoding("UTF-8");
    try {
      XMLWriter writer = new XMLWriter(out, outputFormat);
      writer.write(SnipSerializer.getInstance().serialize(snip));
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out.toString();
  }

  public String getSnip(String name) {
    Snip snip = space.load(name);
    return snip.getContent();
  }

  public String createSnip(String name, String content) {
    Snip snip = space.create(name, content);
    return name;
  }

  public String removeSnip(String name) {
    Snip snip = space.load(name);
    space.remove(snip);
    return name;
  }

  /**
   * Returns the SnipSnap version of this web application
   *
   * @return version version number
   */
  public String getVersion() {
    Globals globals = ConfigurationProxy.getInstance();
    return globals.getVersion();
  }

  /**
   * Authenticate a user. This can be used for single sign on
   * (e.g. the #java.de bot)
   *
   * @param login Login string to test
   * @param passwd Password credential for the given login
   *
   * @return isAuthenticated True when the user can be authenticated
   */
  public boolean authenticateUser(String login, String passwd) throws XmlRpcException {
    User user = authenticationService.authenticate(login, passwd);
    return (null != user);
  }

  // PROTECTED METHODS

  /**
   * Dump the database contents.
   * @return a XML stream containing the dump of the database
   * @throws IOException
   */
  public byte[] dumpXml() throws IOException {
    Configuration config = Application.get().getConfiguration();
    ByteArrayOutputStream exportStream = new ByteArrayOutputStream();
    XMLSnipExport.store(exportStream, space.getAll(), um.getAll(), null, null, config.getFilePath());
    return exportStream.toByteArray();
  }

  public byte[] dumpXml(String match) throws IOException {
    Configuration config = Application.get().getConfiguration();
    ByteArrayOutputStream exportStream = new ByteArrayOutputStream();
    XMLSnipExport.store(exportStream, Arrays.asList(space.match(match)), null, null, null, config.getFilePath());
    return exportStream.toByteArray();
  }

  public boolean restoreXml(byte[] xmlData) throws IOException {
    return restoreXml(xmlData, XMLSnipImport.IMPORT_SNIPS | XMLSnipImport.IMPORT_USERS | XMLSnipImport.OVERWRITE);
  }

  public boolean restoreXml(byte[] xmlData, int flags) throws IOException {
    ByteArrayInputStream importStream = new ByteArrayInputStream(xmlData);
    try {
      XMLSnipImport.load(importStream, flags);
    } catch (Exception e) {
      System.err.println("SnipSnapHandler.restoreXml: unable to import snips: "+e);
      throw new IOException(e.getMessage());
    }
    return true;
  }

  public String install(String prefix, Hashtable appConfig) throws Exception {
    ConfigurationManager configManager = ConfigurationManager.getInstance();
    String appOid = applicationManager.getApplication(prefix);
    Configuration config = configManager.getConfiguration(appOid);

    // only set new values if config does not exits
    if (null == config) {
      config = ConfigurationProxy.newInstance();
      Iterator optionIt = appConfig.keySet().iterator();
      while (optionIt.hasNext()) {
        String option = (String) optionIt.next();
        String value = (String)appConfig.get(option);
        config.set(option, value);
      }
      if (prefix != null && !"".equals(prefix)) {
        if (!prefix.startsWith("/")) {
          prefix = "/" + prefix;
        }
        config.setPrefix(prefix);
      }
      appOid = InitializeDatabase.init(config, new OutputStreamWriter(System.out));
      return configManager.getConfiguration(appOid).getUrl(prefix);
    }

    return "a configuration for '"+prefix+"' already exists, aborting.";
  }

  /**
   * Install a new instance with user name and password. Uses default configuration.
   * @param prefix the instance prefix
   * @param adminLogin admin login name
   * @param passwd admin password
   */
  public String install(String prefix, String adminLogin, String passwd) throws Exception {
    Hashtable appConfig = new Hashtable();
    appConfig.put(Configuration.APP_ADMIN_LOGIN, adminLogin);
    appConfig.put(Configuration.APP_ADMIN_PASSWORD, passwd);
    return install(prefix, appConfig);
  }

  /**
   * Install a new instance with user name and password. Uses the configuration as
   * provided in the command line but overrides the admin user/password found in
   * that file.
   * @param prefix the instance prefix
   * @param adminLogin admin login name
   * @param passwd admin password
   */
  public String install(String prefix, String adminLogin, String passwd, Hashtable appConfig) throws Exception{
    appConfig.put(Configuration.APP_ADMIN_LOGIN, adminLogin);
    appConfig.put(Configuration.APP_ADMIN_PASSWORD, passwd);
    return install(prefix, appConfig);
  }

//  public int removeBacklink(String regexp) {
//    Configuration config = Application.get().getConfiguration();
//    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
//    Iterator allSnipIt = space.getAll().iterator();
//    while(allSnipIt.hasNext()) {
//      Snip snip = (Snip)allSnipIt.next();
//      Links links = snip.getBackLinks();
//      links.iterator();
//    }
//
//  }
}