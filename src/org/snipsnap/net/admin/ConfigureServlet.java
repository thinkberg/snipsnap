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
package org.snipsnap.net.admin;

import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.snip.SnipLink;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ConfigureServlet extends HttpServlet {

  protected final static String ATT_APPLICATION = "app";
  protected final static String ATT_CONFIG = "config";
  protected final static String ATT_EXPERT = "expert";
  protected final static String ATT_FINISH = "finish";
  protected final static String ATT_STEPS = "steps";
  protected final static String ATT_STEP = "step";

  private final static List BASIC_STEPS = Arrays.asList(new String[]{
    "application", "localization", "administrator",
  });

  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);

/*
    EXPERT = new ArrayList(BASIC);
    EXPERT.addAll(
      Arrays.asList(new String[]{
        "permissions", "mail", "proxy", "expert"
      }));
*/
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    HttpSession session = request.getSession();
    Application app = Application.getInstance(session);
    Configuration config = app.getConfiguration();

    if (!config.isInstalled()) {
      session.setAttribute(ATT_APPLICATION, app);
      session.setAttribute(ATT_CONFIG, config);
      List steps = (List)session.getAttribute(ATT_STEPS);
      if(steps == null) {
        steps = BASIC_STEPS;
      }
      if ("true".equals(session.getAttribute(ATT_EXPERT))) {
        //steps = EXPERT;
      } else {
        //steps = BASIC;
      }
      request.setAttribute(ATT_STEPS, steps);

      if (request.getParameter("finish") != null) {
        Map params = request.getParameterMap();
        Iterator iterator = params.keySet().iterator();
        Map paramMap = new HashMap();
        while (iterator.hasNext()) {
          String key = (String) iterator.next();
          String[] values = (String[]) params.get(key);
          paramMap.put(key, values[0]);
        }

        //install(request, response);
      } else {
        String step = request.getParameter("step");
        // checkStep()

        if (null == step || "".equals(step)) {
          step = (String) steps.get(0);
        } else if (request.getParameter("next") != null || request.getParameter("expert") != null) {
          int idx = steps.indexOf(step);
          step = (String) steps.get(idx + 1);
          if(idx + 2 >= steps.size()) {
            request.setAttribute(ATT_FINISH, "true");
          }
        } else if (request.getParameter("previous") != null) {
          int idx = steps.indexOf(step);
          step = (String) steps.get(idx - 1);
        }
        request.setAttribute(ATT_STEP, step);

        if (!request.getContextPath().equals(config.getPath())) {
          config.set(Configuration.APP_PATH, request.getContextPath());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/configure.jsp");
        dispatcher.forward(request, response);
        return;
      }
    }
    response.sendRedirect(SnipLink.absoluteLink("/"));
  }

  private String getNextStep(List steps, String currentStep, String command) {
    return null;

  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
