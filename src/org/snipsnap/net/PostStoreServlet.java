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
import org.snipsnap.snip.*;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * Servlet to store comments.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class PostStoreServlet extends HttpServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String title = request.getParameter("title");
    String content = request.getParameter("content");

    if (request.getParameter("preview") != null) {
      // If there is a title, generate preview of snip with title + content
      if (null != title && !"".equals(title)) {
        request.setAttribute("preview", SnipFormatter.toXML(null, BlogKit.getContent(title, content)));
      } else {
        request.setAttribute("preview", SnipFormatter.toXML(null, content));
      }
      request.setAttribute("content", content);
      request.setAttribute("title", title);
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/post.jsp");
      dispatcher.forward(request, response);
      return;
    } else if (request.getParameter("cancel") == null) {
      HttpSession session = request.getSession();
      Application app = null;
      if (session != null) {
        app = Application.getInstance(session);
        User user = app.getUser();
        if (UserManager.getInstance().isAuthenticated(user)) {
          Blog blog = SnipSpaceFactory.getInstance().getBlog();
          if (null == title || "".equals(title)) {
            blog.post(content);
          } else {
            blog.post(content, title);
          }
        } else {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
      }
    }

    response.sendRedirect(SnipLink.absoluteLink(request, "/space/start"));
  }
}
