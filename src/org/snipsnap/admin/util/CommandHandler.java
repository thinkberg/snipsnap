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
package org.snipsnap.admin.util;

import org.mortbay.jetty.Server;
import org.snipsnap.admin.install.Authenticate;
import org.snipsnap.config.Configuration;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.user.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.File;
import java.util.*;

/**
 * Takes the commands and forwards them to their respective servlet
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class CommandHandler extends HttpServlet {

  private final static String PARAM_OPEN = "free-files";
  private final static String PARAM_CONFIG = "config-file";
  private final static String PARAM_INDEX = "index-page";

  private Set openFiles = new HashSet();
  private String configFile = null;
  private String indexPage = null;

  /**
   * Initialize command handler and set open files and configuration file.
   */
  public void init(ServletConfig servletConfig) throws ServletException {
    String open = servletConfig.getInitParameter(PARAM_OPEN);
    if (open != null) {
      StringTokenizer tokenizer = new StringTokenizer(open, ", ", false);
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        openFiles.add(token);
      }
    }
    configFile = servletConfig.getInitParameter(PARAM_CONFIG);
    indexPage = servletConfig.getInitParameter(PARAM_INDEX);

    if (null == indexPage) {
      indexPage = "/welcome.jsp";
    }
  }

  public final static String ATT_CONFIG = "serverConfig";
  public final static String ATT_SERVERS = "serverServers";
  public final static String ATT_ADMIN = "serverAdmin";

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // get or create session and application object
    HttpSession session = request.getSession(true);

    // check configuration ...
    Configuration config = (Configuration) session.getAttribute(ATT_CONFIG);
    if (null == config) {
      try {
        // prepare configuration and store admin user information to check password against
        Properties defaults = new Properties();
        defaults.load(CommandHandler.class.getResourceAsStream("/conf/snipsnap.conf"));
        config = new Configuration();
        config.load(defaults);
        config.setFile(new File(configFile));
        config.load();
      } catch (IOException e) {
        System.out.println("ATTENTION: unable to load local configuration file: '" + configFile + "'");
      }
    }
    session.setAttribute(ATT_CONFIG, config);
    session.setAttribute(Authenticate.ATT_CHECK_USER,
                         new User(config.getAdminLogin(),
                                  config.getAdminPassword(),
                                  config.getAdminEmail()));

    // get admin user from session, this is null if not authenticated
    User admin = (User) session.getAttribute(ATT_ADMIN);

    String command = request.getPathInfo();
    if (null != config.getAdminLogin() && null == admin &&
        !openFiles.contains(command)) {
      command = "/login.jsp";
    } else {
      Collection servers = Server.getHttpServers();
      session.setAttribute(ATT_SERVERS, servers);
      if (null == command || "/".equals(command)) {
        command = indexPage;
      }
    }

    // set request attribute for the page to include and forward
    request.setAttribute("page", command);
    RequestDispatcher dispatcher = null;
    if (command.endsWith(".jsp")) {
      dispatcher = request.getRequestDispatcher("/main.jsp");
    } else {
      dispatcher = request.getRequestDispatcher(command);
    }

    if (dispatcher != null) {
      response.addHeader("Pragma", "no-cache");
      response.addHeader("Cache-Control", "no-cache, no-store");
      dispatcher.forward(request, response);
    } else {
      Map errors = new HashMap();
      errors.put(command, "Function not implemented!");
      session.setAttribute("errors", errors);
      response.sendRedirect(SnipLink.absoluteLink(request, "/exec"));
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
