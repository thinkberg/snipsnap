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
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.label.Label;
import org.snipsnap.snip.label.LabelManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * Adding a label to a snip
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class StoreLabelServlet extends HttpServlet {

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Configuration config = Application.get().getConfiguration();
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

  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    Configuration config = Application.get().getConfiguration();

    String snipName = request.getParameter("snipname");

    // cancel pressed
    if (null != request.getParameter("cancel")) {
      response.sendRedirect(config.getUrl("/space/" + SnipLink.encode(snipName)));
      return;
    }

    if (null == request.getParameter("back")) {
      Snip snip = ((SnipSpace) Components.getComponent(SnipSpace.class)).load(snipName);
      String labelType = request.getParameter("labeltype");

      Label label = null;
      if (null != labelType) {
        LabelManager manager = (LabelManager) Components.getComponent(LabelManager.class);
        label = manager.getLabel(labelType);
        handleLabel(label, request);
        snip.getLabels().addLabel(label);
        SnipSpaceFactory.getInstance().store(snip);
      }
    }

    response.sendRedirect(config.getUrl("/exec/labels?snipname=" + SnipLink.encode(snipName)));
  }

  private void handleLabel(Label label, HttpServletRequest request) {
    Map params = new HashMap();
    Enumeration enumeration = request.getParameterNames();
    while (enumeration.hasMoreElements()) {
      String name = (String) enumeration.nextElement();
      params.put(name, request.getParameter(name));
    }
    label.handleInput(params);
  }
}
