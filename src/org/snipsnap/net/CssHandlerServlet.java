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
import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.util.ApplicationAwareMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Handler for CSS Stylesheets (virtual)
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class CssHandlerServlet extends HttpServlet {
  private final static String THEME_NAME = "snipsnap.theme.name";

  ApplicationAwareMap styleSheets = new ApplicationAwareMap(HashMap.class, HashMap.class);
  ApplicationAwareMap timeStamps = new ApplicationAwareMap(HashMap.class, HashMap.class);

  private String getStyleSheet(HttpServletRequest request) {
    String theme = getTheme(request);
    String id = request.getPathInfo();
    String themeId = theme + id;
    // get cssSnip for the theme id
    Snip cssSnip = (Snip) styleSheets.getMap().get(themeId);
    Timestamp cssTimestamp = (Timestamp) timeStamps.getMap().get(themeId);
    if (null == cssSnip || null == cssTimestamp || cssSnip.getMTime().after(cssTimestamp)) {
      SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
      String snipName = Configuration.SNIPSNAP_THEMES + "/" + theme + ("/default.css".equals(id) ? "/css" : id);
      cssSnip = space.load(snipName);
      styleSheets.getMap().put(themeId, cssSnip);
      timeStamps.getMap().put(themeId, cssSnip.getMTime().clone());
    }

    return cssSnip.getContent();
  }

  protected long getLastModified(HttpServletRequest request) {
    Timestamp ts = (Timestamp) timeStamps.getMap().get(getTheme(request) + request.getPathInfo());
    return (ts != null ? ts.getTime() / 1000 * 1000 : super.getLastModified(request));
  }

  private String getTheme(HttpServletRequest request) {
    HttpSession session = request.getSession();
    String sessionTheme = (String) session.getAttribute(THEME_NAME);
    Configuration config = Application.get().getConfiguration();
    return sessionTheme == null ? config.getTheme() : sessionTheme;
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    if (request.getParameter("theme") != null) {
      HttpSession session = request.getSession();
      session.setAttribute(THEME_NAME, request.getParameter("theme"));
      timeStamps.getMap().clear();
      String referer = request.getHeader("REFERER");
      if (referer == null || referer.length() == 0) {
        Configuration config = Application.get().getConfiguration();
        referer = config.getSnipUrl(config.getStartSnip());
      }
      response.sendRedirect(referer);
      return;
    }
    try {
      String content = getStyleSheet(request);
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
