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
package org.snipsnap.admin;

import org.snipsnap.snip.SnipLink;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * User Management, Edit a user.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class UserEdit extends HttpServlet {
  /**
   * send request to the actual user manager ...
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session != null) {
      // return to user manager on cancel
      if(request.getParameter("cancel") != null) {
        response.sendRedirect(SnipLink.absoluteLink(request, "/exec/user"));
        return;
      }
      String context = request.getParameter("context");
      RequestDispatcher dispatcher = getServletContext().getContext(context).getNamedDispatcher("org.snipsnap.net.UserManagerServlet");
      dispatcher.forward(request, response);
      session.setAttribute("user", request.getAttribute("user"));
      session.setAttribute("errors", request.getAttribute("errors"));
      if(request.getAttribute("user") != null) {
        dispatcher = request.getRequestDispatcher("/exec/user.jsp");
        dispatcher.forward(request, response);
        session.removeAttribute("errors");
      } else {
        response.sendRedirect(SnipLink.absoluteLink(request, "/exec/user"));
      }
      return;
    }
    response.sendRedirect(SnipLink.absoluteLink(request, "/"));
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}

