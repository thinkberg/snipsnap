package org.snipsnap.net;

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

import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.snip.Blog;
import org.snipsnap.snip.BlogKit;
import org.snipsnap.snip.SnipFormatter;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.Roles;
import org.snipsnap.user.Security;
import org.snipsnap.user.User;
import org.snipsnap.net.filter.MultipartWrapper;
import org.radeox.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to store comments.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class PostStoreServlet extends HttpServlet {
  private static Roles REQUIRED_ROLES;

  static {
    REQUIRED_ROLES = new Roles();
    REQUIRED_ROLES.add(Roles.OWNER);
    REQUIRED_ROLES.add(Roles.EDITOR);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    Configuration config = Application.get().getConfiguration();
    // If this is not a multipart/form-data request continue
    String type = request.getHeader("Content-Type");
    if (type != null && type.startsWith("multipart/form-data")) {
      try {
        request = new MultipartWrapper(request, config.getEncoding() != null ? config.getEncoding() : "UTF-8");
      } catch (IllegalArgumentException e) {
        Logger.warn("PostStoreServlet: multipart/form-data wrapper:" + e.getMessage());
      }
    }

    String title = request.getParameter("title");
    String content = request.getParameter("content");
    String snipName = request.getParameter("name");
    if (null == snipName || "".equals(snipName)) {
      snipName = Application.get().getConfiguration().getStartSnip();
    }

    if (request.getParameter("preview") != null) {
      // If there is a title, generate preview of snipName with title + content
      if (null != title && !"".equals(title)) {
        request.setAttribute("preview", SnipFormatter.toXML(null, BlogKit.getContent(title, content)));
      } else {
        request.setAttribute("preview", SnipFormatter.toXML(null, content));
      }
      request.setAttribute("content", content);
      request.setAttribute("title", title);
      request.setAttribute("param.name", snipName);
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/post.jsp");
      dispatcher.forward(request, response);
      return;
    } else if (request.getParameter("cancel") == null) {
      HttpSession session = request.getSession();
      if (session != null) {
        User user = Application.get().getUser();
//        AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);
        Blog blog = SnipSpaceFactory.getInstance().getBlog(snipName);

        if (Security.hasRoles(user, blog.getSnip(), REQUIRED_ROLES)) {
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

    response.sendRedirect(config.getUrl("/space/" + SnipLink.encode(snipName)));
  }
}
