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
package org.snipsnap.net;

import org.apache.xmlrpc.XmlRpcServer;
import org.apache.xmlrpc.XmlRpc;
import org.picocontainer.PicoContainer;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.Globals;
import org.snipsnap.container.Components;
import org.snipsnap.util.Base64;
import org.snipsnap.xmlrpc.XmlRpcHandler;
import org.xml.sax.SAXException;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Collection;

/**
 * XmlRpc handler servlet
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class XmlRpcServlet extends HttpServlet {
  private XmlRpcServer xmlrpc;
  private boolean initalized = false;

  public void init(ServletConfig servletConfig) throws ServletException {
    xmlrpc = new XmlRpcServer();
  }

  private void initialize() {
    Collection components = Components.findComponents(XmlRpcHandler.class);

    Iterator iterator = components.iterator();
    while (iterator.hasNext()) {
        XmlRpcHandler handler = (XmlRpcHandler) iterator.next();
        xmlrpc.addHandler(handler.getName(), handler);
    }
    initalized = true;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    Globals globals = ConfigurationProxy.getInstance();
    if (!globals.isInstalled()) {
      throw new ServletException("Please finish basic database configuration first.");
    }

    if (!initalized) {
      initialize();
    }

    String auth = request.getHeader("Authorization");
    String login = "", password = "";

    byte[] result = null;
    if (auth != null) {
      auth = new String(Base64.decode(auth.substring(auth.indexOf(' ') + 1)));
      login = auth.substring(0, auth.indexOf(':'));
      password = auth.substring(auth.indexOf(':') + 1);

      result = xmlrpc.execute(request.getInputStream(), login, password);
    } else {
      result = xmlrpc.execute(request.getInputStream());
    }

    response.setContentType("text/xml");
    response.setContentLength(result.length);
    OutputStream out = response.getOutputStream();
    out.write(result);
    out.flush();
  }
}