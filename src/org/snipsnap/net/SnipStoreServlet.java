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

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.filter.SnipFormatter;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.util.mail.InputStreamDataSource;
import org.snipsnap.util.log.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParameterList;
import javax.mail.internet.ContentDisposition;
import javax.mail.Multipart;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * Servlet to store snips into the database after they have been edited.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipStoreServlet extends SnipSnapServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    if(request.getContentType().startsWith("multipart/form-data")) {
      System.out.println(request.getContentType());
      InputStream in = request.getInputStream();
      DataSource ds = new InputStreamDataSource(request.getInputStream(), request.getContentType());
      try {
        MimeMultipart multipart = new MimeMultipart(ds);
        int count = multipart.getCount();
        for(int i = 0; i < count; i++) {
          MimeBodyPart body = (MimeBodyPart)multipart.getBodyPart(i);
          ContentDisposition disp = new ContentDisposition(body.getHeader("Content-disposition", null));
          System.out.println(disp.getParameter("name")+"["+disp.getParameter("filename")+","+body.getContentType()+","+body.getDisposition()+"]: '"+body.getContent()+"'");
        }
      } catch (MessagingException e) {
        System.err.println(e.getMessage());
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }

      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/edit");
      dispatcher.forward(request, response);
      return;
    }

    String name = request.getParameter("name");
    SnipSpace space = SnipSpace.getInstance();
    Snip snip = space.load(name);

    String content = request.getParameter("content");

    if (request.getParameter("preview") != null) {
      request.setAttribute("preview", SnipFormatter.toXML(snip, content));
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/edit");
      dispatcher.forward(request, response);
      return;
    } else if (request.getParameter("cancel") == null) {
      HttpSession session = request.getSession();
      Application app = null;
      if (session != null) {
        app = Application.getInstance(session);
        User user = app.getUser();
        if (UserManager.getInstance().isAuthenticated(user)) {
          if (snip != null) {
            snip.setContent(content);
            space.store(snip);
          } else {
            snip = space.create(name, content);
          }
        } else {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
      }
    } else if (snip == null) {
      // return to referrer if the snip cannot be found
      response.sendRedirect(request.getParameter("referer"));
      return;
    }

    response.sendRedirect(SnipLink.absoluteLink(request, "/space/" + SnipLink.encode(name)));
  }
}
