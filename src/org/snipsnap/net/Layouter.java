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
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.config.Configuration;
import org.snipsnap.util.log.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import sun.misc.Service;

/**
 * Layouter and main handler for web sites.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Layouter extends SnipSnapServlet {

  public final static String ATT_PAGE = "page";

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    /* load all macros found in the services plugin control file */
/*
    Iterator macroIt = Service.providers(HttpServlet.class);
    while(macroIt.hasNext()) {
      try {
        Macro macro = (Macro)macroIt.next();
        add(macro);
        System.err.println("Loaded macro: "+macro.getName());
      } catch (Exception e) {
        System.err.println("MacroFilter: unable to load macro: "+e);
        e.printStackTrace();
      } catch(ServiceConfigurationError err) {
        System.err.println("MacroFilter: error loading macro: "+err);
        err.printStackTrace();
      }
    }
*/

  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {


    // page attribute overrides pathinfo
    String layout = (String)request.getAttribute(ATT_PAGE);
    if(null == layout) {
      layout = SnipLink.decode(request.getPathInfo());
    }

    // why copy? because, getParamMap returns an unmodifyable map
    Map params = request.getParameterMap();
    Iterator iterator = params.keySet().iterator();
    Map paramMap = new HashMap();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      String[] values = (String[]) params.get(key);
      paramMap.put(key, values[0]);
    }
    Application.get().setParameters(paramMap);

    String uri = (String)request.getAttribute("URI");
    Logger.log("URI: "+uri);
    AppConfiguration config = Application.get().getConfiguration();
    if(uri != null) {
      paramMap.put("URI", config.getUrl(uri));
    } else {
      String path = request.getPathInfo();
      paramMap.put("URI", config.getUrl(request.getServletPath()+(path != null ? path : "")));
    }
    paramMap.put("RSS", config.getUrl("/exec/rss"));
    Application.get().setParameters(paramMap);


    if (null == layout || "/".equals(layout)) {
      response.sendRedirect(SnipLink.absoluteLink(request, "/space/start"));
      return;
    }

    request.setAttribute(ATT_PAGE, layout);
    RequestDispatcher dispatcher = null;
    if (layout.endsWith(".jsp")) {
      dispatcher = request.getRequestDispatcher("/main.jsp");
    } else {
      dispatcher = request.getRequestDispatcher(layout);
    }

    if (dispatcher != null) {
      dispatcher.forward(request, response);
      Application.set(null);
    } else {
      response.sendRedirect(SnipLink.absoluteLink(request, "/"));
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
