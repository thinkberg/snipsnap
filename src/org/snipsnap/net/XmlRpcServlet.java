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
import org.snipsnap.xmlrpc.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * XmlRpc handler servlet
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class XmlRpcServlet extends HttpServlet {
  private XmlRpcServer xmlrpc;
  private List handlers;

  public void init(ServletConfig servletConfig) throws ServletException {
    xmlrpc = new XmlRpcServer();

    handlers = new ArrayList();

    // Read via services plugin
    handlers.add(new SnipSnapHandler());
    handlers.add(new MetaWeblogHandler());
    handlers.add(new BloggerHandler());
    handlers.add(new WeblogsPingHandler());
    handlers.add(new GeneratorHandler());
    handlers.add(new WeblogHandler());

    Iterator iterator = handlers.iterator();
    while (iterator.hasNext()) {
      XmlRpcHandler handler = (XmlRpcHandler) iterator.next();
      xmlrpc.addHandler(handler.getName(), handler);
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    byte[] result = xmlrpc.execute(request.getInputStream());
    response.setContentType("text/xml");
    response.setContentLength(result.length);
    OutputStream out = response.getOutputStream();
    out.write(result);
    out.flush();
  }
}