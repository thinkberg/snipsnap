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
import org.snipsnap.snip.XMLSnipImport;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.util.log.Logger;
import org.snipsnap.net.filter.MultipartWrapper;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.mail.internet.MimeBodyPart;
import javax.mail.BodyPart;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Servlet to import into the database.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ImportServlet extends HttpServlet {
  private final static String OK_IMPORTED = "Import OK!";
  private final static String ERR_IOEXCEPTION = "Error while importing!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    Application.getInstance(session);
    User admin = session != null ? (User)session.getAttribute(AdminServlet.ATT_ADMIN) : null;
    if(null == admin || !(request instanceof MultipartWrapper)) {
      response.sendRedirect(SnipLink.absoluteLink(request, "/manager"));
      return;
    }

    Map errors = new HashMap();
    request.setAttribute("errors", errors);

    MultipartWrapper req = (MultipartWrapper)request;

    boolean overwrite = request.getParameter("overwrite") != null;
    String data[] = request.getParameterValues("data");
    int importMask = 0;
    for(int i = 0; i < data.length; i++) {
      if("users".equals(data[i])) {
        importMask = importMask | XMLSnipExport.USERS;
      }
      if("snips".equals(data[i])) {
        importMask = importMask | XMLSnipExport.SNIPS;
      }
    }

    BodyPart file = req.getBodyPart("input");

    System.err.println("Disabling weblogs ping and jabber notification ...");
    AppConfiguration config = Application.get().getConfiguration();
    String ping = config.getProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_WEBLOGS_PING);
    String noty = config.getProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_NOTIFICATION);
    config.setProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_WEBLOGS_PING, "deny");
    config.setProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_NOTIFICATION, "deny");

    try {
      XMLSnipImport.load(file.getInputStream(), importMask);
      errors.put("message", OK_IMPORTED);
    } catch (Exception e) {
      System.err.println("ImportServlet: unable to import snips: "+e);
      e.printStackTrace();
      errors.put("message", ERR_IOEXCEPTION);
    }

    System.err.println("Resetting weblogs ping and jabber notification to config settings ...");
    config.setProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_WEBLOGS_PING, ping);
    config.setProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_NOTIFICATION, noty);

    RequestDispatcher dispatcher = request.getRequestDispatcher("/manager/import.jsp");
    dispatcher.forward(request, response);
  }
}
