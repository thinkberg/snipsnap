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

import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.snip.SnipSpaceFactory;
import snipsnap.api.label.Label;
import org.snipsnap.snip.label.LabelManager;
import org.snipsnap.container.Components;
import org.snipsnap.net.filter.MultipartWrapper;
import org.radeox.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Adding a label to a snip
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class AddLabelServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    Configuration config = snipsnap.api.app.Application.get().getConfiguration();
    // If this is not a multipart/form-data request continue
    String type = request.getHeader("Content-Type");
    if (type != null && type.startsWith("multipart/form-data")) {
      try {
        request = new MultipartWrapper(request, config.getEncoding() != null ? config.getEncoding() : "UTF-8");
      } catch (IllegalArgumentException e) {
        Logger.warn("AddLabelServlet: multipart/form-data wrapper:" + e.getMessage());
      }
    }

    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    snipsnap.api.config.Configuration config = Application.get().getConfiguration();

    String snipName = request.getParameter("snipname");
    if (null == snipName) {
      response.sendRedirect(config.getUrl(config.getStartSnip()));
      return;
    }
    // cancel pressed
    if (null != request.getParameter("cancel")) {
      response.sendRedirect(config.getUrl("/exec/labels?snipname=" + snipsnap.api.snip.SnipLink.encode(snipName)));
      return;
    }

    Snip snip = snipsnap.api.snip.SnipSpaceFactory.getInstance().load(snipName);
    request.setAttribute("snip", snip);

    String labelType = request.getParameter("labeltype");
    LabelManager manager = (LabelManager)Components.getComponent(LabelManager.class);
    snipsnap.api.label.Label label = manager.getLabel(labelType);
    request.setAttribute("label", label);

    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/addlabel.jsp");
    dispatcher.forward(request, response);
  }

}
