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

import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;
import org.snipsnap.net.filter.MultipartWrapper;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipFormatter;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to store comments.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class CommentStoreServlet extends HttpServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    Configuration config = Application.get().getConfiguration();
    // If this is not a multipart/form-data request continue
    String type = request.getHeader("Content-Type");
    if (type != null && type.startsWith("multipart/form-data")) {
      try {
        request = new MultipartWrapper(request, config.getEncoding() != null ? config.getEncoding() : "UTF-8");
      } catch (IllegalArgumentException e) {
        Logger.warn("CommentStoreServlet: multipart/form-data wrapper:" + e.getMessage());
      }
    }

    String name = request.getParameter("comment");
    String content = request.getParameter("content");
    Snip snip = SnipSpaceFactory.getInstance().load(name);

    if (request.getParameter("preview") != null) {
      request.setAttribute("snip", snip);
      request.setAttribute("preview", SnipFormatter.toXML(snip, content));
      request.setAttribute("content", content);
      request.setAttribute("comment", name);
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/comment.jsp");
      dispatcher.forward(request, response);
      return;
    } else if (request.getParameter("cancel") == null) {

      HttpSession session = request.getSession();
      if (session != null) {
        User user = Application.get().getUser();
        AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);

        if (snip != null && service.isAuthenticated(user)) {
          snip.getComments().postComment(content);
        } else {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
          return;
        }
      }
    } else if (snip == null) {
      // return to referrer if the snip cannot be found
      response.sendRedirect(request.getParameter("REFERER"));
      return;
    }

    response.sendRedirect(config.getUrl("/comments/" + SnipLink.encode(name)));
  }
}
