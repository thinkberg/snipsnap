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

import org.apache.lucene.search.Hits;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Load a snip to view.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipSearchServlet extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    String query = request.getParameter("query");
    if (query != null && query.length() > 0) {
      HttpSession session = request.getSession(true);
      SnipSpace space = SnipSpace.getInstance();
      Hits hits = space.search(query);
      session.setAttribute("query", query);
      session.setAttribute("hits", hits);
      session.setAttribute("startIndex", new Integer(0));
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/search.jsp");
      dispatcher.forward(request, response);
      return;
    }
    response.sendRedirect(SnipLink.absoluteLink(request, "/"));
  }

}
