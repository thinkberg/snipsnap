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
package org.snipsnap.net.filter;

import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A ServletFilter that takes care of uninstalled web applications and creating the
 * application object and parameter information for servlets down the chain.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class InitFilter implements Filter {

  private FilterConfig config = null;

  public void init(FilterConfig config) throws ServletException {
    this.config = config;
  }

  public void destroy() {
    config = null;
  }

  public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // make sure it's an http servlet request
    HttpServletRequest request = (HttpServletRequest) req;

    // get or create session and application object
    // create/get instance of current application
    HttpSession session = request.getSession(true);
    Application app = Application.getInstance(session);
    AppConfiguration config = app.getConfiguration();

    //System.out.println("request: "+request);

//    String serverName = request.getServerName();
//    int serverPort = request.getServerPort();
//    //System.out.println("server: "+serverName+":"+serverPort);
//    String requestURL = request.getRequestURL().toString();
//    //System.out.println("request url: "+requestURL);
//    int postServerNameIndex = requestURL.indexOf(serverName) + serverName.length();
//    String contextPath = request.getContextPath();
//    if(contextPath.length() == 0) {
//      config.setUrl(requestURL.substring(0, requestURL.indexOf("/", postServerNameIndex)));
//    } else {
//      config.setUrl(requestURL.substring(0, requestURL.indexOf(contextPath, postServerNameIndex) + contextPath.length()));
//    }
//    //System.out.println("url: "+config.getUrl());

    // make sure the request has a correct character encoding
    // the enc-wrapper ensures some methods return correct strings too
    try {
      request.setCharacterEncoding(config.getEncoding());
      request = new EncRequestWrapper(request, request.getCharacterEncoding());
    } catch (UnsupportedEncodingException e) {
      // do nothing ...
    }

    // get an instance of the user manager and check for a logged in user
    UserManager um = UserManager.getInstance();

    User user = app.getUser();
    if (user == null) {
      user = um.getUser(request, (HttpServletResponse)response);
    }

    app.setUser(user, session);
    session.setAttribute("app", app);
    session.setAttribute("space", SnipSpaceFactory.getInstance());

    // why copy? because, getParamMap returns an unmodifyable map
    Map params = request.getParameterMap();
    Iterator iterator = params.keySet().iterator();
    Map paramMap = new HashMap();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      String[] values = (String[]) params.get(key);
      paramMap.put(key, values[0]);
    }

    String path = request.getPathInfo();
    String uri = (String) request.getAttribute("URI");
    if (uri != null) {
      paramMap.put("URI", config.getUrl(uri));
    } else {
      paramMap.put("URI", config.getUrl(request.getServletPath() + (path != null ? path : "")));
    }
    paramMap.put("RSS", config.getUrl("/exec/rss"));
    app.setParameters(paramMap);

    // make sure we do not enter the default web application unless it's fully installed
    if(!config.isInstalled() && !path.startsWith("/install")) {
      System.out.println("InitFilter: "+config.getName()+" is not configured, redirecting");
      ((HttpServletResponse)response).sendRedirect(request.getServletPath() + "/install");
      return;
    }

    // apply the chain
    chain.doFilter(request, response);
  }
}