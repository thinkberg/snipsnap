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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.text.DateFormat;

/**
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ConfigureServlet extends HttpServlet {

  protected final static String ATT_APPLICATION = "app";
  protected final static String ATT_CONFIG = "config";
  protected final static String ATT_ADVANCED = "advanced";
  protected final static String ATT_USAGE = "usage";
  protected final static String ATT_FINISH = "finish";
  protected final static String ATT_STEPS = "steps";
  protected final static String ATT_STEP = "step";
  protected final static String ATT_ERROR = "error";

  private final static List BASIC_STEPS = Arrays.asList(new String[]{
    "application", /*"theme",*/ "localization", "administrator",
  });

  private final static List EXPERT_STEPS = Arrays.asList(new String[]{
    "permissions", "mail", "moblog", "proxy", "database", "expert"
  });

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    HttpSession session = request.getSession();
    Application app = Application.getInstance(session);
    Configuration config = app.getConfiguration();

    session.setAttribute(ATT_APPLICATION, app);
    session.setAttribute(ATT_CONFIG, config);
    List steps = (List) session.getAttribute(ATT_STEPS);
    if (steps == null) {
      steps = new ArrayList(BASIC_STEPS);
    }
    if (session.getAttribute(ATT_USAGE) == null) {
      if (config.allow(Configuration.APP_PERM_REGISTER) &&
        config.allow(Configuration.APP_PERM_WEBLOGSPING)) {
        session.setAttribute(ATT_USAGE, "public");
      } else if (!config.allow(Configuration.APP_PERM_REGISTER)) {
        session.setAttribute(ATT_USAGE, "closed");
      } else if (!config.allow(Configuration.APP_PERM_WEBLOGSPING)) {
        session.setAttribute(ATT_USAGE, "intranet");
      } else {
        session.setAttribute(ATT_USAGE, "custom");
      }
    }

    if (!config.isInstalled()) {
      if(session.getAttribute("DEFAULTS") == null) {
        Locale locale = request.getLocale();
        config.set(Configuration.APP_COUNTRY, locale.getCountry());
        config.set(Configuration.APP_LANGUAGE, locale.getLanguage());
        int offset = TimeZone.getDefault().getRawOffset() / (60 * 60 *  1000);
        String id = "GMT" + (offset >= 0 ? "+" : "") + offset;
        config.set(Configuration.APP_TIMEZONE, TimeZone.getTimeZone(id).getID());
        config.set(Configuration.APP_WEBLOGDATEFORMAT, "");
        session.setAttribute("DEFAULTS", "true");
      }
      String step = request.getParameter("step");
      if (null == step || "".equals(step)) {
        step = (String) steps.get(0);
      } else {
        if (checkStep(step, request, session)) {
          if (request.getParameter("finish") != null) {
            Map params = request.getParameterMap();
            Iterator iterator = params.keySet().iterator();
            Map paramMap = new HashMap();
            while (iterator.hasNext()) {
              String key = (String) iterator.next();
              String[] values = (String[]) params.get(key);
              paramMap.put(key, values[0]);
            }

            try {
              install(config);
              response.sendRedirect(config.getUrl());
              return;
            } catch (Exception e) {
              request.setAttribute(ATT_ERROR, "FATAL: Unable to configure. See server log for details");
              e.printStackTrace();
            }
          } else {
            if (request.getParameter("next") != null) {
              int idx = steps.indexOf(step);
              // if we see a "next" and this is the end it must be expert settings
              if (null != request.getParameter(ATT_ADVANCED)) {
                session.setAttribute(ATT_ADVANCED, "true");
                steps = addSteps(steps, EXPERT_STEPS);
              }
              step = (String) steps.get(idx + 1);
              if (idx + 2 >= steps.size()) {
                request.setAttribute(ATT_FINISH, "true");
              }
            } else if (request.getParameter("previous") != null) {
              int idx = steps.indexOf(step);
              step = (String) steps.get(idx - 1);
            }
          }
        }
      }

      if (!request.getContextPath().equals(config.getPath())) {
        config.set(Configuration.APP_PATH, request.getContextPath());
      }

      session.setAttribute(ATT_STEP, step);
      session.setAttribute(ATT_STEPS, steps);
      RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/configure.jsp");
      dispatcher.forward(request, response);
      return;
    }
    response.sendRedirect(SnipLink.absoluteLink("/"));
  }

  private void install(Configuration config) throws Exception {
    System.out.println("Config: " + config);
  }

  private boolean checkStep(String step, HttpServletRequest request, HttpSession session) {

    return true;
  }

  private List addSteps(List steps, List toAdd) {
    Iterator it = toAdd.iterator();
    while (it.hasNext()) {
      String step = (String) it.next();
      if (!steps.contains(step)) {
        steps.add(step);
      }
    }
    return steps;
  }

}
