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
import org.snipsnap.xmlrpc.BloggerHandler;
import org.snipsnap.xmlrpc.WeblogsPingHandler;
import org.snipsnap.xmlrpc.SnipSnapHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;


/**
 * XmlRpc handler servlet
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class XmlRpcServlet extends HttpServlet {

    public void init(ServletConfig servletConfig) throws ServletException {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        //System.out.println("XMLRPC call received.");
        XmlRpcServer xmlrpc = new XmlRpcServer();

        xmlrpc.addHandler("snipSnap", new SnipSnapHandler());
        xmlrpc.addHandler("blogger", new BloggerHandler());
        xmlrpc.addHandler("weblogUpdates", new WeblogsPingHandler());
        byte[] result = xmlrpc.execute(request.getInputStream());
        response.setContentType("text/xml");
        response.setContentLength(result.length);
        OutputStream out = response.getOutputStream();
        out.write(result);
        out.flush();
    }
}