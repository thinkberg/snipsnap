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
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

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
  private final static List FREE_METHODS = Arrays.asList(new String[]{
    "getVersion",
    "authenticateUser"
  });

  private final static List PREFIX_METHODS = Arrays.asList(new String[]{
    "dumpXml",
    "restoreXml",
  });

  public static final String API_PREFIX = "snipSnap";

  private AuthenticationService authenticationService;

  public SnipSnapHandler(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
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
      ApplicationManager appManager = (ApplicationManager) Components.getComponent(ApplicationManager.class);
      String prefix = "/";
      String appOid = appManager.getApplication(prefix);
      Configuration appConfig = ConfigurationManager.getInstance().getConfiguration(appOid);
      if (appConfig != null) {
        vector.remove(0);
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
    UserManager um = (UserManager) Components.getComponent(UserManager.class);
    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
    XMLSnipExport.store(exportStream, space.getAll(), um.getAll(), config.getFilePath());
    return exportStream.toByteArray();
  }

  public byte[] dumpXml(String match) throws IOException {
    Configuration config = Application.get().getConfiguration();
    ByteArrayOutputStream exportStream = new ByteArrayOutputStream();
    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
    XMLSnipExport.store(exportStream, Arrays.asList(space.match(match)), null, config.getFilePath());
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
      e.printStackTrace();
    }
    return true;
  }
}