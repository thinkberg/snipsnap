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
import org.snipsnap.user.User;
import org.snipsnap.snip.XMLSnipExport;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.container.Components;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.beans.XMLEncoder;

/**
 * Handles XML-RPC calls for the SnipSnap API
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipSnapHandler extends XmlRpcSupport {
  public static final String API_PREFIX = "snipSnap";

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
    User user = authenticate(login, passwd);
    return (null != user);
  }

  public byte[] exportDatabase() throws IOException {
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

  public void importDatabase(byte[] xmlData) throws IOException {
//    ByteArrayInputStream importStream = new ByteArrayInputStream(xmlData);
//    Document importDocument =
  }
}