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
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.container.Components;
import org.snipsnap.config.Configuration;
import org.snipsnap.app.Application;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for CSS Stylesheets (virtual)
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class CssHandlerServlet extends HttpServlet {

  Map styleSheets = new HashMap();
  Map timeStamps = new HashMap();

  private String getStyleSheet(String id) {
    Snip cssSnip = (Snip)styleSheets.get(id);
    Timestamp cssTimestamp = (Timestamp)timeStamps.get(id);
    if (null == cssSnip || cssSnip.getMTime().after(cssTimestamp)) {
      SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
      Configuration config = Application.get().getConfiguration();
      String snipName = Configuration.SNIPSNAP_THEMES + "/" + config.getTheme() + ("/default.css".equals(id) ? "/css" : id);
      cssSnip = space.load(snipName);
      styleSheets.put(id, cssSnip);
      timeStamps.put(id, cssSnip.getMTime().clone());
    }

    return cssSnip.getContent();
  }

  protected long getLastModified(HttpServletRequest request) {
    Timestamp ts = (Timestamp) timeStamps.get(request.getServletPath());
    return (ts != null ? ts.getTime() / 1000 * 1000 : super.getLastModified(request));
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    try {
      String content = getStyleSheet(request.getServletPath());
      response.setContentType("text/css");
      response.setContentLength(content.length());
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
      writer.write(content);
      writer.flush();
    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

  }
}
