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
import org.snipsnap.container.Components;
import org.picocontainer.PicoContainer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * XmlRpc handler servlet
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class XmlRpcServlet extends HttpServlet {
  private XmlRpcServer xmlrpc;

  public void init(ServletConfig servletConfig) throws ServletException {
    xmlrpc = new XmlRpcServer();

    PicoContainer container = Components.getContainer();
    Iterator iterator = container.getComponentInstances().iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof XmlRpcHandler) {
        XmlRpcHandler handler = (XmlRpcHandler) o;
        xmlrpc.addHandler(handler.getName(), handler);
      }
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