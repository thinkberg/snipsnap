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

import org.radeox.util.logging.LogHandler;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.app.ApplicationManager;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationManager;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.Globals;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.container.Components;
import org.snipsnap.container.SessionService;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.Digest;
import org.snipsnap.user.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
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
  // private final static String
  private Globals globals = null;

  public void init(FilterConfig filterConfig) throws ServletException {
    ServletContext context = filterConfig.getServletContext();

    // check servlet context and then local servlet parameter or assume WEB-INF
    String configParam = (String) context.getAttribute(ServerConfiguration.INIT_PARAM);
    if (null == configParam) {
      BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/defaults/copyright.txt")));
      try {
        String line;
        while (null != (line = br.readLine())) {
          System.out.println(line);
        }
      } catch (IOException e) {
        // create copyright output if the copyrights file was not found
        System.out.println("SnipSnap (unknown version)");
        System.out.println("Copyright (c) 2003 Fraunhofer Gesellschaft");
        System.out.println("Fraunhofer Institute for Computer Architecture and Software Technology");
        System.out.println("All Rights Reserved. See License Agreement for terms and conditions of use.");
      }

      String initParam = context.getInitParameter(ServerConfiguration.INIT_PARAM);
      if (null != initParam) {
        configParam = context.getRealPath(initParam);
      }
    }
    if (null == configParam) {
      configParam = context.getRealPath("/WEB-INF/application.conf");
    }


    // create globals configuration by getting an instance and loading application.conf
    globals = ConfigurationProxy.getInstance();
    try {
      globals.loadGlobals(new FileInputStream(configParam));
    } catch (Exception e) {
      System.err.println("InitFilter: unable to load globals: " + configParam + ": " + e.getMessage());
    }

    String installKey = globals.getInstallKey();
    if (null == installKey || "".equals(installKey)) {
      globals.setInstallKey(Digest.getDigest("" + new Date()).substring(0, 5).toLowerCase());
      System.out.println(">> Your installation key is '" + globals.getInstallKey() + "'");
      System.out.println(">> Remember it, you will need this key to setup SnipSnap.");
      try {
        globals.storeGlobals(new FileOutputStream(configParam));
      } catch (Exception e) {
        System.err.println("InitFilter: unable to store install key: " + e.getMessage());
      }
    }

    globals.setWebInfDir(new File(context.getRealPath("WEB-INF")));

    // initalize logger before starting to load configurations
    String logger = globals.getLogger();
    try {
      Logger.setHandler((LogHandler) Class.forName(logger).newInstance());
    } catch (Exception e) {
      System.err.println("InitFilter: LogHandler not found: " + logger);
    }

    if (!globals.isInstalled()) {
      System.out.println(">> Please finish the installation, visit");
      System.out.println(">> " + ((Configuration) globals).getUrl() + "?key=" + globals.getInstallKey());
    } else {
      loadApplicationContexts();
    }
  }

  private void loadApplicationContexts() {
    ApplicationManager appManager = (ApplicationManager) Components.getComponent(ApplicationManager.class);
    Collection prefixes = appManager.getPrefixes();
    Iterator prefixIt = prefixes.iterator();
    Application app = Application.get();
    boolean weblogsPing = false;
    while (prefixIt.hasNext()) {
      String prefix = (String) prefixIt.next();
      String appOid = appManager.getApplication(prefix);
      app.storeObject(Application.OID, appOid);

      System.out.print(">> Loading: " + prefix + " ");
      Configuration appConfig = ConfigurationProxy.newInstance();
      SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
      if (space.exists(Configuration.SNIPSNAP_CONFIG)) {
        Snip configSnip = space.load(Configuration.SNIPSNAP_CONFIG);
        String configContent = configSnip.getContent();
        try {
          appConfig.load(new ByteArrayInputStream(configContent.getBytes()));
          System.out.print("(" + appConfig.getName() + ", " + appConfig.getUrl() + ")");
        } catch (IOException e) {
          System.out.print("ERROR: " + e.getMessage());
          continue;
        }
        ConfigurationManager.getInstance().addConfiguration(appOid, appConfig);
      } else {
        System.out.print("(NOT CONFIGURED)");
      }
      weblogsPing = appConfig.allow(Configuration.APP_PERM_WEBLOGSPING);
      System.out.println();
    }
    if (weblogsPing) {
      System.out.println(">> WARNING: Weblogs ping is enabled.\n" +
                         ">> This means that SnipSnap sends notifications to hosts on the internet\n" +
                         ">> when your weblog changes. To turn this off take a look at the FAQ at\n" +
                         ">> http://snipsnap.org/space/faq");
    }
    System.out.println(">> Installation key: " + globals.getInstallKey());
  }

  public void destroy() {
    //config = null;
  }

  public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // make sure it's an http servlet request
    HttpServletRequest request = (HttpServletRequest) req;

    try {
      request = new EncRequestWrapper(request, globals.getEncoding() != null ? globals.getEncoding() : "UTF-8");
    } catch (UnsupportedEncodingException e) {
      Logger.log(Logger.FATAL, "InitFilter: unsupported encoding '" + req.getCharacterEncoding() + "'", e);
    }

    String path = request.getServletPath();

    // make sure XML-RPC is handled directly
    if (path.startsWith("/RPC2")) {
      if (globals.isInstalled()) {
        chain.doFilter(request, response);
      } else {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                                                   "Please finish database installation first.");
      }
      return;
    }

    HttpSession session = request.getSession();
    Application app = Application.get();
    ConfigurationManager configManager = ConfigurationManager.getInstance();
    ApplicationManager appManager = null;

    if (globals.isInstalled()) {
      appManager = (ApplicationManager) Components.getComponent(ApplicationManager.class);
    }

    String prefix = "/";
    String appOid = null;

    // we need the path again, to make sure it is encoded correctly
    path = request.getServletPath();

    Configuration appConfig = null;
    // get application manager and application oid is possible
    if (globals.isInstalled()) {
      appOid = appManager.getApplication(prefix);
      appConfig = configManager.getConfiguration(appOid);
      app.setConfiguration(appConfig);
      app.storeObject(Application.OID, appOid);
    }

    // configure the url (base context path) for the current request
    if (appConfig != null && "true".equals(appConfig.getRealAutodetect())) {
      // apache proxies provide the host and port of the original request
      // however, they do not provide the context path, TODO maybe Apache 2
      String xForwardedHost = request.getHeader("X-Forwarded-Host");
      if (xForwardedHost != null) {
        int colonIndex = xForwardedHost.indexOf(':');
        String host = xForwardedHost;
        String port = null;
        if (colonIndex != -1) {
          host = host.substring(0, colonIndex);
          port = xForwardedHost.substring(colonIndex + 1);
        }
        appConfig.set(Configuration.APP_REAL_HOST, host);
        appConfig.set(Configuration.APP_REAL_PORT, port == null ? "80" : port);
      } else {
        String host = request.getServerName();
        String port = "" + request.getServerPort();
        appConfig.set(Configuration.APP_REAL_HOST, host);
        appConfig.set(Configuration.APP_REAL_PORT, port);
        appConfig.set(Configuration.APP_REAL_PATH, request.getContextPath());
      }
      // System.err.println("autoconfigured url: " + config.getUrl());
    }

    //System.out.println("appManager: "+appManager+", appConfig="+appConfig+", "+(appConfig != null ? ""+appConfig.isConfigured() : "not configured"));
    // make sure we do not enter the default web application unless it's fully installed
    if (null == appManager || null == appConfig || !appConfig.isConfigured()) {
      if (path == null || !(path.startsWith("/admin") || path.startsWith("/images"))) {
        String queryString = request.getQueryString();
        queryString = (null == queryString || "".equals(queryString) ? "" : queryString);
        ((HttpServletResponse) response).sendRedirect(request.getContextPath() + "/admin/configure?" + queryString);
        return;
      }
    } else {
      if (!"/".equals(prefix)) {
        path = path.substring(prefix.length());
      }

      session.setAttribute("app", app);
      session.setAttribute("space", SnipSpaceFactory.getInstance());

      // check for a logged in user
      SessionService service = (SessionService) Components.getComponent(SessionService.class);
      User user = service.getUser(request, (HttpServletResponse) response);
      app.setUser(user, session);

      Iterator paramIt = request.getParameterMap().keySet().iterator();
      Map paramMap = new HashMap();
      while(paramIt.hasNext()) {
        String key = (String)paramIt.next();
        paramMap.put(key, request.getParameter(key));
      }
      String uri = (String) request.getAttribute("URI");
      if (uri != null) {
        paramMap.put("URI", appConfig.getUrl(uri));
      } else {
        String pathInfo = request.getPathInfo();
        paramMap.put("URI", appConfig.getUrl((path != null ? path : "") +
                                             (pathInfo != null ? pathInfo : "")));
      }
      paramMap.put("RSS", appConfig.getUrl("/exec/rss"));
      app.setParameters(paramMap);
    }

    // If this is not a multipart/form-data request continue
    String type = request.getHeader("Content-Type");
    if (type != null && type.startsWith("multipart/form-data")) {
      try {
        request = new MultipartWrapper(request, globals.getEncoding() != null ? globals.getEncoding() : "UTF-8");
      } catch (IllegalArgumentException e) {
        Logger.warn("InitFilter: multipart/form-data wrapper:" + e.getMessage());
      }
    }

    // apply the chain
    chain.doFilter(request, response);
  }
}
