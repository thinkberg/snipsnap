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
package org.snipsnap.server;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;
import java.net.MalformedURLException;
import java.io.IOException;

public class AdminXmlRpcClient {
  XmlRpcClient xmlRpcClient = null;

  public AdminXmlRpcClient(String host, int port, String password) throws MalformedURLException  {
    String xmlRpcUrl = "http://" + host + (port != 80 ? ":" + port : "") + "/";
    xmlRpcClient = new XmlRpcClient(xmlRpcUrl);
    xmlRpcClient.setBasicAuthentication("admin", password);
  }

  public void install(String name, String host, String port, String path) throws XmlRpcException, IOException {
    Vector args = new Vector();
    args.addElement(name);
    args.addElement(host);
    args.addElement(port);
    args.addElement(path);
    xmlRpcClient.execute("install", args);
  }

  public void delete(String name) throws XmlRpcException, IOException {
    Vector args = new Vector();
    args.addElement(name);
    xmlRpcClient.execute("delete", args);
  }
}
