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
import org.snipsnap.net.filter.MultipartWrapper;
import org.snipsnap.snip.SnipLink;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ConfigureServlet extends HttpServlet {

  protected final static String ATT_APPLICATION = "app";
  protected final static String ATT_CONFIG = "config";
  protected final static String ATT_VALIDATOR = "validator";
  protected final static String ATT_ADVANCED = "advanced";
  protected final static String ATT_USAGE = "usage";
  protected final static String ATT_FINISH = "finish";
  protected final static String ATT_STEPS = "steps";
  protected final static String ATT_STEP = "step";
  protected final static String ATT_ERROR = "error";

  protected final static String STEP_APPLICATION = "application";
  protected final static String STEP_THEME = "theme";
  protected final static String STEP_LOCALIZATION = "localization";
  protected final static String STEP_ADMINISTRATOR = "administrator";
  protected final static String STEP_PERMISSIONS = "permissions";
  protected final static String STEP_MAIL = "mail";
  protected final static String STEP_MOBLOG = "moblog";
  protected final static String STEP_PROXY = "proxy";
  protected final static String STEP_DATABASE = "database";
  protected final static String STEP_EXPERT = "expert";

  private final static List BASIC_STEPS = Arrays.asList(new String[]{
    STEP_APPLICATION,
    /*"theme",*/
    STEP_LOCALIZATION,
    STEP_ADMINISTRATOR,
  });

  private final static List EXPERT_STEPS = Arrays.asList(new String[]{
    STEP_PERMISSIONS,
    STEP_MAIL,
    STEP_MOBLOG,
    STEP_PROXY,
    STEP_DATABASE,
    STEP_EXPERT,
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
      } else if (!config.allow(Configuration.APP_PERM_REGISTER) &&
        !config.allow(Configuration.APP_PERM_WEBLOGSPING)) {
        session.setAttribute(ATT_USAGE, "closed");
      } else if (!config.allow(Configuration.APP_PERM_WEBLOGSPING)) {
        session.setAttribute(ATT_USAGE, "intranet");
      } else {
        session.setAttribute(ATT_USAGE, "custom");
      }
    }

    if (!config.isInstalled()) {
      if (session.getAttribute("DEFAULTS") == null) {
        Locale locale = request.getLocale();
        if (null != locale.getCountry() && !"".equals(locale.getCountry())) {
          config.set(Configuration.APP_COUNTRY, locale.getCountry());
        }
        if (null != locale.getLanguage() && !"".equals(locale.getLanguage())) {
          config.set(Configuration.APP_LANGUAGE, locale.getLanguage());
        }
        int offset = TimeZone.getDefault().getRawOffset() / (60 * 60 * 1000);
        String id = "GMT" + (offset >= 0 ? "+" : "") + offset;
        config.set(Configuration.APP_TIMEZONE, TimeZone.getTimeZone(id).getID());
        session.setAttribute("DEFAULTS", "true");
      }
      String step = request.getParameter("step");
      if (null == step || "".equals(step)) {
        step = (String) steps.get(0);
      } else {
        List errors = checkStep(step, steps, request, config);
        if (errors.size() == 0) {
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
        } else {
          request.setAttribute("errors", errors);
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

  private List checkStep(String step, List steps, HttpServletRequest request, Configuration config) {
    List errors = new ArrayList();
    if (STEP_APPLICATION.equals(step)) {
      setupApplication(request, config, errors, steps);
    } else if (STEP_THEME.equals(step)) {
      setupTheme(request, config, errors);
    } else if (STEP_LOCALIZATION.equals(step)) {
      setupLocalization(request, config, errors);
    } else if (STEP_PERMISSIONS.equals(step)) {

    }
    return errors;
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

  private void setupApplication(HttpServletRequest request, Configuration config, List errors, List steps) {
    config.setName(request.getParameter(Configuration.APP_NAME));
    config.setTagline(request.getParameter(Configuration.APP_TAGLINE));
    if (request instanceof MultipartWrapper) {
      MultipartWrapper mpRequest = (MultipartWrapper) request;
      try {
        String fileName = mpRequest.getFileName(Configuration.APP_LOGO); 
        if (fileName != null && !"".equals(fileName)) {
          String logoFileName = mpRequest.getFileName(Configuration.APP_LOGO);
          String logoFileType = mpRequest.getFileContentType(Configuration.APP_LOGO);
          if(logoFileType.startsWith("image")) {
            InputStream logoFileIs = mpRequest.getFileInputStream(Configuration.APP_LOGO);
            File root = new File(getServletContext().getRealPath("/images"));
            FileOutputStream imageOut = new FileOutputStream(new File(root, logoFileName));
            byte buffer[] = new byte[8192];
            int len = 0;
            while ((len = logoFileIs.read(buffer)) != -1) {
              imageOut.write(buffer, 0, len);
            }
            imageOut.close();
            logoFileIs.close();
            config.setLogo(logoFileName);
          } else {
            errors.add(Configuration.APP_LOGO+".type");
          }
        }
      } catch (IOException e) {
        errors.add(Configuration.APP_LOGO);
        e.printStackTrace();
      }
    }

    String usage = request.getParameter("usage");
    if("public".equals(usage)) {
      config.setPermRegister("allow");
      config.setPermWeblogsPing("allow");
    } else if("closed".equals(usage)) {
      config.setPermRegister("deny");
      config.setPermWeblogsPing("deny");
    } else if("intranet".equals(usage)) {
      config.setPermWeblogsPing("deny");
    } else {
      if(!steps.contains(STEP_PERMISSIONS)) {
        steps.add(STEP_PERMISSIONS);
      }
      request.getSession().setAttribute(ATT_USAGE, "custom");
    }
    String name = config.getName();
    if (null == name || "".equals(name)) {
      errors.add(Configuration.APP_NAME);
    }
  }

  private void setupTheme(HttpServletRequest request, Configuration config, List errors) {

  }

  private final static List countries = Arrays.asList(Locale.getISOCountries());
  private final static List languages = Arrays.asList(Locale.getISOLanguages());

  private void setupLocalization(HttpServletRequest request, Configuration config, List errors) {
    String country = request.getParameter(Configuration.APP_COUNTRY);
    if (countries.contains(country)) {
      config.setCountry(country);
    } else {
      errors.add(Configuration.APP_COUNTRY);
    }

    String language = request.getParameter(Configuration.APP_LANGUAGE);
    if (languages.contains(language)) {
      config.setLanguage(language);
    } else {
      errors.add(Configuration.APP_LANGUAGE);
    }
    config.setTimezone(request.getParameter(Configuration.APP_TIMEZONE));
    config.setWeblogDateFormat(request.getParameter(Configuration.APP_WEBLOGDATEFORMAT));
    try {
      DateFormat df = new SimpleDateFormat(config.getWeblogDateFormat());
      df.format(new Date());
    } catch (Exception e) {
      errors.add(Configuration.APP_WEBLOGDATEFORMAT);
    }
    String geoCoordinates = request.getParameter(Configuration.APP_GEOCOORDINATES);
    if (null != geoCoordinates && !"".equals(geoCoordinates)) {
      config.setGeoCoordinates(geoCoordinates);
      int commaIdx = geoCoordinates.indexOf(',');
      if (commaIdx > 0) {
        String latStr = geoCoordinates.substring(0, commaIdx).trim();
        String lonStr = geoCoordinates.substring(commaIdx+1).trim();
        try {
          long latitude = Long.parseLong(latStr);
          long longitude = Long.parseLong(lonStr);
          if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
            config.setGeoCoordinates(geoCoordinates);
          } else {
            errors.add(Configuration.APP_GEOCOORDINATES + ".range");
            return;
          }
        } catch (NumberFormatException e) {
          errors.add(Configuration.APP_GEOCOORDINATES + ".format");
          e.printStackTrace();
          return;
        }
      } else {
        errors.add(Configuration.APP_GEOCOORDINATES);
        return;
      }
    }
  }


}