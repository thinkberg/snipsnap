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
package org.snipsnap.net.admin;

import org.snipsnap.app.Application;
import org.snipsnap.snip.HomePage;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.XMLSnipExport;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.config.AppConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Servlet to export database.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ExportServlet extends HttpServlet {
  private final static String OK_EXPORTED = "Export OK!";
  private final static String ERR_WRONG_OUTPUT = "Don't know where to output data!";
  private final static String ERR_IOEXCEPTION = "Error while writing data!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String output = request.getParameter("output");
    String data[] = request.getParameterValues("data");
    HttpSession session = request.getSession();
    session.removeAttribute("errors");
    Map errors = new HashMap();
    session.setAttribute("errors", errors);

    OutputStream out = null;
    if("application".equals(output)) {
      AppConfiguration config = Application.get().getConfiguration();
      File outFile = new File(config.getFile().getParentFile(), config.getName()+".snip");
      out = new FileOutputStream(outFile);
    } else if("web".equals(output)) {
      response.setContentType("text/xml");
      out = response.getOutputStream();
    } else {
      errors.put("message", ERR_WRONG_OUTPUT);
      response.sendRedirect(SnipLink.absoluteLink(request, "/manager/export.jsp"));
      return;
    }

    int exportMask = 0;
    for(int i = 0; i < data.length; i++) {
      if("users".equals(data[i])) {
        exportMask = exportMask | XMLSnipExport.USERS;
      }
      if("snips".equals(data[i])) {
        exportMask = exportMask | XMLSnipExport.SNIPS;
      }
    }

    try {
      XMLSnipExport.store(out, exportMask);
    } catch (IOException e) {
      errors.put("message", ERR_IOEXCEPTION);
      response.sendRedirect(SnipLink.absoluteLink(request, "/manager/export.jsp"));
      return;
    }

    if("application".equals(output)) {
      out.close();
      errors.put("message", OK_EXPORTED);
      response.sendRedirect(SnipLink.absoluteLink(request, "/manager/export.jsp"));
    }
  }
}
