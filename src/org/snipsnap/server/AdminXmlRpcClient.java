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

import org.apache.xmlrpc.DefaultXmlRpcTransport;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.XmlRpcTransport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

public class AdminXmlRpcClient {
  protected XmlRpcClient xmlRpcClient = null;
  private DefaultXmlRpcTransport xmlRpcTransport = null;

  public AdminXmlRpcClient(String url, String user, String password) throws MalformedURLException {
    this(new URL(url), user, password);
  }

  public AdminXmlRpcClient(URL url, String user, String password) throws MalformedURLException  {
    URL xmlRpcUrl = new URL(url, "RPC2");
    xmlRpcClient = new XmlRpcClient(xmlRpcUrl);
    xmlRpcTransport = new DefaultXmlRpcTransport(xmlRpcUrl);
    xmlRpcTransport.setBasicAuthentication(user != null ? user : "admin", password != null ? password : "");
  }

  public Object execute(String method, Vector args) throws XmlRpcException, IOException {
    return xmlRpcClient.execute(new XmlRpcRequest(method, args), xmlRpcTransport);
  }

  public Hashtable getApplications() throws XmlRpcException, IOException {
    Vector args = new Vector();
    return (Hashtable)execute("getApplications", args);
  }

  public void shutdown() throws XmlRpcException, IOException {
    xmlRpcClient.execute("shutdown", new Vector());
  }

  public URL install(String name, String host, String port, String path) throws XmlRpcException, IOException {
    //System.out.println("install("+name+","+host+","+port+","+path+")");
    Vector args = new Vector();
    args.addElement(name);
    args.addElement(host);
    args.addElement(port);
    args.addElement(path);
    return new URL((String) execute("install", args));
  }

  public void delete(String name, boolean backup) throws XmlRpcException, IOException {
    //System.out.println("delete(" + name + "," + backup +")");
    Vector args = new Vector();
    args.addElement(name);
    args.addElement(new Boolean(backup));
    execute("delete", args);
  }
}
