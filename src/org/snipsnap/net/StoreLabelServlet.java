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
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.label.Label;
import org.snipsnap.snip.label.LabelManager;

import javax.servlet.ServletException;
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
public class StoreLabelServlet extends SnipSnapServlet {

  protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
    doGet(httpServletRequest, httpServletResponse);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String snipName = request.getParameter("snip.name");

    // cancel pressed
    if (null != request.getParameter("cancel")) {
      response.sendRedirect(SnipLink.absoluteLink(request, "/space/" + SnipLink.encode(snipName)));
      return;
    }

    String type = request.getParameter("label.type");
    if (null != type) {
      LabelManager manager = LabelManager.getInstance();
      Label label = manager.getLabel(type);
      Map params = new HashMap();
      Enumeration enumeration = request.getParameterNames();
      while (enumeration.hasMoreElements()) {
        String name = (String) enumeration.nextElement();
        params.put(name, request.getParameter(name));
      }
      label.handleInput(params);
      Snip snip = SnipSpace.getInstance().load(snipName);
      snip.getLabels().addLabel(label);
    }
    response.sendRedirect(SnipLink.absoluteLink(request, "/space/" + SnipLink.encode(snipName)));
  }
}