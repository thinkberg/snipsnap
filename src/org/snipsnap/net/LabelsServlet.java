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
  protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    throws ServletException, IOException {
    doGet(httpServletRequest, httpServletResponse);
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

    // display all labels of current Snip:
    StringBuffer labelsProxy = new StringBuffer();
    labelsProxy.append("<table border=\"1\" cellpadding=\"2\" cellspacing=\"2\">");
    labelsProxy.append("<tr><th> Name </th><th> Type </th><th> Value </th></tr>");
    Labels labels = snip.getLabels();
    Iterator labelsIt = labels.getIds().iterator();
    while (labelsIt.hasNext()) {
      Label lbl = labels.getLabel((String) labelsIt.next());
      labelsProxy.append("<tr><td>");
      labelsProxy.append(lbl.getName());
      labelsProxy.append("</td><td>");
      labelsProxy.append(lbl.getType());
      labelsProxy.append("</td><td>");
      labelsProxy.append(lbl.getValue());
      labelsProxy.append("</td><td>");
      labelsProxy.append("[<a href=\"");
      labelsProxy.append(SnipLink.getExecRoot());
      labelsProxy.append("/removelabel?snipname=");
      labelsProxy.append(snip.getNameEncoded());
      labelsProxy.append("&amp;labelname=");
      labelsProxy.append(lbl.getName());
      labelsProxy.append("\">remove</a>]");
      labelsProxy.append("</td></tr>");
    }
    labelsProxy.append("</table>");
    request.setAttribute("labelsProxy", labelsProxy.toString());

    // selection of label type for adding a new label:
    StringBuffer typesProxy = new StringBuffer();
    typesProxy.append("Choose label type:<br/><select name=\"labeltype\">");

    LabelManager manager = (LabelManager)Components.getComponent(LabelManager.class);
    Iterator typesIt = manager.getTypes().iterator();
    while (typesIt.hasNext()) {
      String labelType = (String) typesIt.next();
      typesProxy.append("<option>");
      typesProxy.append(labelType);
      typesProxy.append("</option>");
    }
    typesProxy.append("</select>");
    request.setAttribute("typesProxy", typesProxy.toString());

    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/showlabels.jsp");
    dispatcher.forward(request, response);
  }
}
