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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.snip.label.Label;
import org.snipsnap.app.Application;
import org.snipsnap.container.Components;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

/**
 * Create a new Snip
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipNewServlet extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);

    String parent = request.getParameter("parent");
    String parentBefore = request.getParameter("parentBefore");
    if (null == parentBefore) {
      parentBefore = parent;
    }
    String content = request.getParameter("content");
    String name = request.getParameter("name");
    String template = request.getParameter("template");
	  String copy = request.getParameter("copy.template");
    if ((copy != null) && (template != null)) {
      Snip snip = space.load(template);
      content = snip.getContent();
    }

    request.setAttribute("parent", parent);
    request.setAttribute("parentBefore", parentBefore);
    request.setAttribute("content", content);
    request.setAttribute("name", name);
    request.setAttribute("templates", getTemplates());
    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/new.jsp");
    dispatcher.forward(request, response);
  }

  private List getTemplates() {
    List templates = new ArrayList();

    SnipSpace snipspace = (SnipSpace) Components.getComponent(SnipSpace.class);
    List snipList = snipspace.getAll();

    Iterator iterator = snipList.iterator();
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      Labels labels = snip.getLabels();
      boolean noLabelsAll = labels.getAll().isEmpty();

      if (!noLabelsAll) {
        Collection labelsCat = labels.getLabels("TypeLabel");
        if (!labelsCat.isEmpty()) {
          Iterator iter = labelsCat.iterator();
          while (iter.hasNext()) {
            Label label = (Label) iter.next();
            if (label.getValue().equals("Template")) {
              templates.add(snip.getName());
            }
          }
        }
      }
    }
    return templates;
  }
}
