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
package org.snipsnap.admin;

import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.net.filter.EncRequestWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class AdminInitFilter implements Filter {

  private final static String DEFAULT_ENCODING = "UTF-8";
  protected final static String ATT_CONFIG = "installer.config";

  Properties serverConfig = new Properties();

  public void init(FilterConfig config) throws ServletException {
    try {
      serverConfig.load(new FileInputStream("conf/server.conf"));
    } catch (IOException e) {
      System.err.println("AdminInitFilter: unable to load server config: "+e);
    }
  }

  public void destroy() {
    //config = null;
  }

  public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // make sure it's an http servlet request
    HttpServletRequest request = (HttpServletRequest) req;

    // make sure the request has a correct character encoding
    // the enc-wrapper ensures some methods return correct strings too
    try {
      request.setCharacterEncoding(DEFAULT_ENCODING);
      request = new EncRequestWrapper(request, request.getCharacterEncoding());
    } catch (UnsupportedEncodingException e) {
      System.err.println("AdminInitFilter: unsupported encoding: "+e);
    }

    // get or create session and application object
    HttpSession session = request.getSession(false);

    String path = request.getServletPath();
    System.out.println("path="+path);
    if(path == null || !(path.startsWith("/images") || path.endsWith(".css"))) {
      // check authentication and verify session
      if (null == session || null == session.getAttribute(ATT_CONFIG)) {
        String serverPass = serverConfig.getProperty(ServerConfiguration.ADMIN_PASS);
        String installPass = path;
        if(installPass == null || "".equals(installPass)) {
          installPass = "/" + request.getParameter("password");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");

        if (installPass == null || "".equals(installPass) ||
          !serverPass.equals(installPass.substring(1))) {
          request.setAttribute("step", "login");
        } else {
          session = request.getSession(true);
          session.setAttribute(ATT_CONFIG, serverConfig);
          request.setAttribute("step", "install");
        }
        dispatcher.forward(request, response);
        return;
      }
    }
    System.out.println("AdminInitFilter: "+path);

    // apply the chain
    chain.doFilter(request, response);
  }

}
