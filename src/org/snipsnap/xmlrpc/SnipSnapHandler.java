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
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.snip.XMLSnipExport;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.User;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Handles XML-RPC calls for the SnipSnap API
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipSnapHandler extends AuthXmlRpcHandler implements XmlRpcHandler {
  private final static List FREE_METHODS = Arrays.asList(new String[] {
    "getName",
    "getVersion",
    "authenticateUser"
  });

  public static final String API_PREFIX = "snipSnap";

  private AuthenticationService authenticationService;

  public SnipSnapHandler(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  protected boolean authenticate(String username, String password) {
    User user = authenticationService.authenticate(username, password);
    if (user != null && user.isAdmin()) {
      Application.get().setUser(user);
      return true;
    }
    Logger.warn("XML-RPC authenticate: invalid login for " + username);
    return false;
  }

  public Object execute(String method, Vector vector, String user, String password) throws Exception {
    if(FREE_METHODS.contains(method)) {
      return super.execute(method, vector);
    }
    return super.execute(method, vector, user, password);
  }

  public String getName() {
    return API_PREFIX;
  }

  /**
   * Returns the SnipSnap version of this running instance
   *
   * @return version Version number of this running instance
   */
  public String getVersion() {
    return Application.get().getConfiguration().getVersion();
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
    ByteArrayOutputStream exportStream = new ByteArrayOutputStream();
    Document exportDocument = XMLSnipExport.getBackupDocument();
    StreamResult streamResult = new StreamResult(exportStream);
    TransformerFactory tf = SAXTransformerFactory.newInstance();
    try {
      Transformer serializer = tf.newTransformer();
      serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      serializer.transform(new DOMSource(exportDocument), streamResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return exportStream.toByteArray();
  }

  public void restoreXml(byte[] xmlData) throws IOException {
//    ByteArrayInputStream importStream = new ByteArrayInputStream(xmlData);
//    Document importDocument =
  }
}