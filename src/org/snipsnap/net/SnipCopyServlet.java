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
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.net.filter.MultipartWrapper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Copy Snips
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipCopyServlet extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    Configuration config = Application.get().getConfiguration();
    // If this is not a multipart/form-data request continue
    String type = request.getHeader("Content-Type");
    if (type != null && type.startsWith("multipart/form-data")) {
      try {
        request = new MultipartWrapper(request, config.getEncoding() != null ? config.getEncoding() : "UTF-8");
      } catch (IllegalArgumentException e) {
        Logger.warn("SnipCopyServlet: multipart/form-data wrapper:" + e.getMessage());
      }
    }

    String name = request.getParameter("snip");

    if(null != request.getParameter("cancel")) {
      response.sendRedirect(config.getSnipUrl(name));
      return;
    }

    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);

    if (null != name && space.exists(name)) {
      Snip snip = space.load(name);
      if (request.getParameter("copy") != null) {
        String newName = request.getParameter("name");
        if(newName != null && newName.endsWith("/")) {
          newName = newName.substring(0, newName.length() - 2);
        }
        String[] subsnips = request.getParameterValues("subsnips");

        Snip newSnip = snip.copy(newName);
        Logger.log("SnipCopyServlet: copied " + snip.getName() + " to " + newSnip.getName());
        for(int s = 0; subsnips != null && s < subsnips.length; s++) {
          String subSnipName = subsnips[s];
          Snip subSnip = space.load(subSnipName);
          if(subSnip != null && subSnipName.startsWith(name)) {
            String newSubSnipName = newName + "/" + subsnips[s].substring(name.length()+1);
            Snip newSubSnip = subSnip.copy(newSubSnipName);
            Logger.log("SnipCopyServlet: copied "+subSnip.getName()+" to "+newSubSnip.getName());
          } else {
            Logger.warn("SnipCopyServlet: snip does not exist: "+subsnips[s]);
          }
        }
        response.sendRedirect(config.getUrl("/space/" + SnipLink.encode(newName)));
        return;
      }
      request.setAttribute("name", name);
      request.setAttribute("snip", snip);
      request.setAttribute("subsnips", Arrays.asList(space.match(snip.getName() + "/")));

      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/copy.jsp");
      dispatcher.forward(request, response);
      return;
    }

    String referer = request.getHeader("REFERER");
    if (referer == null || referer.length() == 0) {
      referer = config.getSnipUrl(config.getStartSnip());
    }
    response.sendRedirect(referer);
  }

}
