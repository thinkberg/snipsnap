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
import org.snipsnap.snip.label.Label;
import org.snipsnap.snip.label.LabelManager;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;
import org.snipsnap.net.filter.MultipartWrapper;
import org.radeox.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

/**
 * Showing the labels of a snip
 * @author Marco Mosconi
 * @version $Id$
 */
public class LabelsServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
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

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    Configuration config = Application.get().getConfiguration();

    String snipName = request.getParameter("snipname");
    if (null == snipName) {
      response.sendRedirect(config.getUrl("/space/" + config.getStartSnip()));
      return;
    }

    // cancel pressed
    if (null != request.getParameter("cancel")) {
      response.sendRedirect(config.getUrl("/space/" + SnipLink.encode(snipName)));
      return;
    }

    Snip snip = SnipSpaceFactory.getInstance().load(snipName);
    request.setAttribute("snip", snip);

    LabelManager manager = (LabelManager) Components.getComponent(LabelManager.class);
    request.setAttribute("labelmanager", manager);

    if(null != request.getParameter("add")) {
      String labelType = request.getParameter("labeltype");
      Label label = manager.getLabel(labelType);
      request.setAttribute("label", label);
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/addlabel.jsp");
      dispatcher.forward(request, response);
      return;
    }

    if(null != request.getParameter("edit")) {
      request.setAttribute("edit", "edit");
      String labelName = request.getParameter("labelname");
      String labelValue = request.getParameter("labelvalue");
      Label label = snip.getLabels().getLabel(labelName, labelValue);
      if(null != label) {
        request.setAttribute("label", label);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/addlabel.jsp");
        dispatcher.forward(request, response);
        return;
      }
    }

    if(null != request.getParameter("delete")) {
      String[] labels = request.getParameterValues("label");
      for (int i = 0; i < labels.length; i++) {
        String label[] = labels[i].split("\\|");
        snip.getLabels().removeLabel(label[0], label[1]);
      }
    }

    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/showlabels.jsp");
    dispatcher.forward(request, response);
  }
}
