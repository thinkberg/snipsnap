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
import org.snipsnap.server.AdminServer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class AdminInitFilter implements Filter {

  private final static String DEFAULT_ENCODING = "UTF-8";

  protected final static String ATT_CONFIG = "config";
  protected final static String ATT_STEP = "step";


  protected final static String PARAM_INSTALL = "install";
  protected final static String PARAM_HOST = "app.host";
  protected final static String PARAM_PORT = "app.port";
  protected final static String PARAM_PATH = "app.path";

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
    if(null == path || "".equals(path)) {
      System.out.println("Redirecting '"+path+"' -> "+ request.getContextPath() + "/");
      ((HttpServletResponse)response).sendRedirect(request.getContextPath()+"/");
      return;
    }

    // except css files and images everything is protected
    if(!(path.startsWith("/images") || path.endsWith(".css"))) {
      RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
      String step = null;

      // check authentication and verify session
      if (null == session || null == session.getAttribute(ATT_CONFIG)) {
        String serverPass = serverConfig.getProperty(ServerConfiguration.ADMIN_PASS);
        String installPass = path;
        if(installPass == null || "".equals(installPass) || "/".equals(installPass)) {
          installPass = "/" + request.getParameter("password");
        }

        if (installPass == null || "".equals(installPass) || serverPass.equals(installPass.substring(1))) {
          step = "login";
        }
      }

      if(null == step) {
        if(null == request.getParameter(PARAM_INSTALL)) {
          step = "install";
        } else {
          String url = install(request.getParameter(PARAM_HOST),
                               request.getParameter(PARAM_PORT),
                               request.getParameter(PARAM_PATH));
          ((HttpServletResponse)response).sendRedirect(url);
          return;
        }
      }

      request.setAttribute(ATT_STEP, step);
      session.setAttribute(ATT_CONFIG, serverConfig);
      session = request.getSession(true);
      dispatcher.forward(request, response);
      return;
    }

    // apply the chain
    chain.doFilter(request, response);
  }

  protected String install(String host, String port, String path) {
    int adminPort = Integer.parseInt(serverConfig.getProperty(ServerConfiguration.ADMIN_PORT));
    String args = (null == host ? "" : host) + (null == port ? "" : ":"+port) + (null == path ? "" : " "+path);
    AdminServer.execute(adminPort, AdminServer.CMD_INSTALL, args);
    return "";
  }
}
