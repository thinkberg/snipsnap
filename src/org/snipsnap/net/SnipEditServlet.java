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
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Load a snip to edit. Loads the snip into the request context. In case
 * the snip is newly created put the name into "snip_name".
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipEditServlet extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    final String name = request.getParameter("name");
    if (null == name) {
      response.sendRedirect(SnipLink.absoluteLink(request, "/space/start"));
      return;
    }

    Snip snip = SnipSpaceFactory.getInstance().load(name);
    request.setAttribute("snip", snip);
    request.setAttribute("snip_name", name);

    AppConfiguration config = Application.get().getConfiguration();
    File imageDir = new File(config.getFile().getParentFile().getParentFile(), "images");

    final Map ids = new HashMap();
    final int prefixLength = ("image-" + name + "-").length();
    String[] images = imageDir.list(new FilenameFilter() {
      public boolean accept(File dir, String file) {
        if (file.startsWith("image-" + name) && file.length() > prefixLength) {
          String ext = file.substring(file.indexOf('.', prefixLength) + 1);
          if ("png".equals(ext)) {
            ids.put(file, file.substring(prefixLength, file.indexOf('.', prefixLength)));
          } else {
            ids.put(file, file.substring(prefixLength));
          }
          return true;
        }
        return false;
      }
    });
    if (images != null) {
      request.setAttribute("ids", ids);
      request.setAttribute("images", Arrays.asList(images));
    }

    String content = (String) request.getParameter("content");
    if (null != content) {
      request.setAttribute("content", content);
    } else {
      request.setAttribute("content", snip != null ? snip.getContent() : "");
    }

    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/edit.jsp");
    dispatcher.forward(request, response);
  }

}
