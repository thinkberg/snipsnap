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
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.InitializeDatabase;
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
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
  protected final static String ATT_ERRORS = "errors";

  protected final static String STEP_APPLICATION = "application";
  protected final static String STEP_THEME = "theme";
  protected final static String STEP_ADMINISTRATOR = "administrator";
  protected final static String STEP_LOCALIZATION = "localization";
  protected final static String STEP_PERMISSIONS = "permissions";
  protected final static String STEP_MAIL = "mail";
  protected final static String STEP_MOBLOG = "moblog";
  protected final static String STEP_PROXY = "proxy";
  protected final static String STEP_DATABASE = "database";
  protected final static String STEP_EXPERT = "expert";
  protected final static String STEP_FINISH = "finish";

  private final static List BASIC_STEPS = Arrays.asList(new String[]{
    STEP_APPLICATION,
    /*STEP_THEME,*/
    STEP_ADMINISTRATOR,
    STEP_FINISH,
  });

  private final static List EXPERT_STEPS = Arrays.asList(new String[]{
    STEP_LOCALIZATION,
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
    Configuration config = (Configuration) session.getAttribute(ATT_CONFIG);
    if (null == config) {
      config = ConfigurationProxy.newInstance();
      config.setWebInfDir(Application.get().getConfiguration().getWebInfDir());
    }

    session.setAttribute(ATT_APPLICATION, app);
    session.setAttribute(ATT_CONFIG, config);
    List steps = (List) session.getAttribute(ATT_STEPS);
    if (steps == null) {
      steps = new ArrayList(BASIC_STEPS);
    }
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

    if (!config.isInstalled()) {
      if (session.getAttribute("DEFAULTS") == null) {
        Locale locale = request.getLocale();
        if (null != locale.getCountry() && !"".equals(locale.getCountry())) {
          config.set(Configuration.APP_COUNTRY, locale.getCountry());
        }
        if (null != locale.getLanguage() && !"".equals(locale.getLanguage())) {
          config.set(Configuration.APP_LANGUAGE, locale.getLanguage());
        }
        // divide offset (ms) by an hour
        int offset = TimeZone.getDefault().getRawOffset() / (60 * 60 * 1000);
        String id = "GMT" + (offset >= 0 ? "+" : "") + offset;
        config.set(Configuration.APP_TIMEZONE, TimeZone.getTimeZone(id).getID());
        session.setAttribute("DEFAULTS", "true");
      }

      String step = request.getParameter("step");
      if (null == step || "".equals(step)) {
        step = (String) steps.get(0);
      } else {
        Map errors = checkStep(step, steps, request, config);
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

            // save current config
            Configuration currentConfig = Application.get().getConfiguration();
            try {
              config.setWebInfDir(currentConfig.getWebInfDir());
              InitializeDatabase.setOutput(new OutputStreamWriter(System.out));
              InitializeDatabase.init(config);
              response.sendRedirect(config.getUrl());
              return;
            } catch (Exception e) {
              // restore config just to be sure
              Application.get().setConfiguration(currentConfig);
              errors.put("fatal", "fatal");
              e.printStackTrace();
            }
          } else {
            if (request.getParameter("next") != null) {
              int idx = steps.indexOf(step);
              // if we see a "next" and this is the end it must be expert settings
              if (null != request.getParameter(ATT_ADVANCED)) {
                session.setAttribute(ATT_ADVANCED, "true");
                if (request.getParameter("advanced.all") != null) {
                  steps = addSteps(steps, EXPERT_STEPS);
                } else {
                  List list = new ArrayList();
                  Iterator it = request.getParameterMap().keySet().iterator();
                  while (it.hasNext()) {
                    String advStep = (String) it.next();
                    if (advStep.startsWith("advanced.step.")) {
                      advStep = advStep.substring("advanced.step.".length());
                      list.add(advStep);
                    }
                  }
                  addSteps(steps, list);
                }
                step = (String) steps.get(idx);
              } else {
                step = (String) steps.get(idx + 1);
              }
            } else if (request.getParameter("previous") != null) {
              int idx = steps.indexOf(step);
              step = (String) steps.get(idx - 1);
            }
          }
        } else {
          request.setAttribute(ATT_ERRORS, errors);
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


  private Map checkStep(String step, List steps, HttpServletRequest request, Configuration config) {
    Map errors = new HashMap();
    if (STEP_APPLICATION.equals(step)) {
      setupApplication(request, config, errors, steps);
    } else if (STEP_THEME.equals(step)) {
      setupTheme(request, config, errors);
    } else if (STEP_LOCALIZATION.equals(step)) {
      setupLocalization(request, config, errors);
    } else if (STEP_ADMINISTRATOR.equals(step)) {
      setupAdministrator(request, config, errors);
    } else if (STEP_PERMISSIONS.equals(step)) {
      setupPermissions(request, config, errors);
    } else if (STEP_MAIL.equals(step)) {
      setupMail(request, config, errors);
    } else if (STEP_MOBLOG.equals(step)) {
      setupMoblog(request, config, errors);
    } else if (STEP_PROXY.equals(step)) {
      setupProxy(request, config, errors);
    } else if (STEP_DATABASE.equals(step)) {
      setupDatabase(request, config, errors);
    } else if (STEP_EXPERT.equals(step)) {
      setupExpert(request, config, errors);
    }
    return errors;
  }

  private List addSteps(List steps, List toAdd) {
    steps.remove(STEP_FINISH);
    Iterator it = toAdd.iterator();
    while (it.hasNext()) {
      String step = (String) it.next();
      if (!steps.contains(step)) {
        steps.add(step);
      }
    }
    steps.add(STEP_FINISH);
    return steps;
  }

  private void setupApplication(HttpServletRequest request, Configuration config, Map errors, List steps) {
    config.setName(request.getParameter(Configuration.APP_NAME));
    config.setTagline(request.getParameter(Configuration.APP_TAGLINE));
    if (request instanceof MultipartWrapper) {
      MultipartWrapper mpRequest = (MultipartWrapper) request;
      try {
        String fileName = mpRequest.getFileName(Configuration.APP_LOGO);
        if (fileName != null && !"".equals(fileName)) {
          String logoFileName = mpRequest.getFileName(Configuration.APP_LOGO);
          String logoFileType = mpRequest.getFileContentType(Configuration.APP_LOGO);
          if (logoFileType.startsWith("image")) {
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
            errors.put(Configuration.APP_LOGO, Configuration.APP_LOGO + ".type");
          }
        }
      } catch (IOException e) {
        errors.put(Configuration.APP_LOGO, Configuration.APP_LOGO);
        e.printStackTrace();
      }
    }

    String usage = request.getParameter("usage");
    if ("public".equals(usage)) {
      config.setPermRegister("allow");
      config.setPermWeblogsPing("allow");
    } else if ("closed".equals(usage)) {
      config.setPermRegister("deny");
      config.setPermWeblogsPing("deny");
    } else if ("intranet".equals(usage)) {
      config.setPermWeblogsPing("deny");
    } else {
      if (!steps.contains(STEP_PERMISSIONS)) {
        steps.add(steps.size() - 1, STEP_PERMISSIONS);
      }
      request.getSession().setAttribute(ATT_USAGE, "custom");
    }
    String name = config.getName();
    if (null == name || "".equals(name)) {
      errors.put(Configuration.APP_NAME, Configuration.APP_NAME);
    }
  }

  private void setupTheme(HttpServletRequest request, Configuration config, Map errors) {

  }

  private final static List countries = Arrays.asList(Locale.getISOCountries());
  private final static List languages = Arrays.asList(Locale.getISOLanguages());

  private void setupLocalization(HttpServletRequest request, Configuration config, Map errors) {
    String country = request.getParameter(Configuration.APP_COUNTRY);
    if (countries.contains(country)) {
      config.setCountry(country);
    } else {
      errors.put(Configuration.APP_COUNTRY, Configuration.APP_COUNTRY);
    }

    String language = request.getParameter(Configuration.APP_LANGUAGE);
    if (languages.contains(language)) {
      config.setLanguage(language);
    } else {
      errors.put(Configuration.APP_LANGUAGE, Configuration.APP_LANGUAGE);
    }
    config.setTimezone(request.getParameter(Configuration.APP_TIMEZONE));
    config.setWeblogDateFormat(request.getParameter(Configuration.APP_WEBLOGDATEFORMAT));
    try {
      DateFormat df = new SimpleDateFormat(config.getWeblogDateFormat());
      df.format(new Date());
    } catch (Exception e) {
      errors.put(Configuration.APP_WEBLOGDATEFORMAT, Configuration.APP_WEBLOGDATEFORMAT);
    }
    String geoCoordinates = request.getParameter(Configuration.APP_GEOCOORDINATES);
    if (null != geoCoordinates && !"".equals(geoCoordinates)) {
      config.setGeoCoordinates(geoCoordinates);
      int commaIdx = geoCoordinates.indexOf(',');
      if (commaIdx > 0) {
        String latStr = geoCoordinates.substring(0, commaIdx).trim();
        String lonStr = geoCoordinates.substring(commaIdx + 1).trim();
        if (latStr.length() == 0 || lonStr.length() == 0) {
          errors.put(Configuration.APP_GEOCOORDINATES, Configuration.APP_GEOCOORDINATES);
        } else {
          try {
            long latitude = Long.parseLong(latStr);
            long longitude = Long.parseLong(lonStr);
            if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
              config.setGeoCoordinates(geoCoordinates);
            } else {
              errors.put(Configuration.APP_GEOCOORDINATES, Configuration.APP_GEOCOORDINATES + ".range");
            }
          } catch (NumberFormatException e) {
            errors.put(Configuration.APP_GEOCOORDINATES, Configuration.APP_GEOCOORDINATES + ".format");
            e.printStackTrace();
          }
        }
      } else {
        errors.put(Configuration.APP_GEOCOORDINATES, Configuration.APP_GEOCOORDINATES);
      }
    }
  }

  private void setupAdministrator(HttpServletRequest request, Configuration config, Map errors) {
    String login = request.getParameter(Configuration.APP_ADMIN_LOGIN);
    config.setAdminLogin(login);
    if (null == login || "".equals(login)) {
      errors.put(Configuration.APP_ADMIN_LOGIN, Configuration.APP_ADMIN_LOGIN);
    }
    String password = request.getParameter(Configuration.APP_ADMIN_PASSWORD);
    String verify = request.getParameter(Configuration.APP_ADMIN_PASSWORD + ".vrfy");
    if ((password != null && password.length() > 0) || config.getAdminPassword() == null) {
      if (password == null || password.length() == 0) {
        errors.put(Configuration.APP_ADMIN_PASSWORD, Configuration.APP_ADMIN_PASSWORD);
      } else if (!password.equals(verify)) {
        errors.put(Configuration.APP_ADMIN_PASSWORD, Configuration.APP_ADMIN_PASSWORD + ".match");
      } else if (password.length() < 3) {
        errors.put(Configuration.APP_ADMIN_PASSWORD, Configuration.APP_ADMIN_PASSWORD + ".length");
      } else {
        config.setAdminPassword(password);
      }
    }
    config.setAdminEmail(request.getParameter(Configuration.APP_ADMIN_EMAIL));
  }

  private String allowDeny(String value) {
    if ("allow".equals(value)) {
      return value;
    } else {
      return "deny";
    }
  }

  private void setupPermissions(HttpServletRequest request, Configuration config, Map errors) {
    config.setPermRegister(allowDeny(request.getParameter(Configuration.APP_PERM_REGISTER)));
    config.setPermWeblogsPing(allowDeny(request.getParameter(Configuration.APP_PERM_WEBLOGSPING)));
    config.setPermNotification(allowDeny(request.getParameter(Configuration.APP_PERM_NOTIFICATION)));
    config.setPermExternalImages(allowDeny(request.getParameter(Configuration.APP_PERM_EXTERNALIMAGES)));
    config.setPermMultiplePosts(allowDeny(request.getParameter(Configuration.APP_PERM_MULTIPLEPOSTS)));
  }

  private void setupMail(HttpServletRequest request, Configuration config, Map errors) {
    String mailHost = request.getParameter(Configuration.APP_MAIL_HOST);
    config.setMailHost(mailHost);
    if (null != mailHost && !"".equals(mailHost)) {
      try {
        // check host name/address
        final InetAddress address = InetAddress.getByName(mailHost);
        Socket socket = new Socket();
        try {
          socket.connect(new InetSocketAddress(address, 25), 5 * 1000);
          socket.close();
        } catch (IOException e) {
          errors.put(Configuration.APP_MAIL_HOST, Configuration.APP_MAIL_HOST + ".connect");
        }
      } catch (UnknownHostException e) {
        errors.put(Configuration.APP_MAIL_HOST, Configuration.APP_MAIL_HOST + ".unknown");
      }
    }

    String mailDomain = request.getParameter(Configuration.APP_MAIL_DOMAIN);
    config.setMailDomain(mailDomain);
    if (config.getMailHost() != null && !"".equals(config.getMailHost())) {
      if (mailDomain == null || "".equals(mailDomain) || mailDomain.indexOf('@') != -1) {
        errors.put(Configuration.APP_MAIL_DOMAIN, Configuration.APP_MAIL_DOMAIN);
      }
    }
  }

  private void setupMoblog(HttpServletRequest request, Configuration config, Map errors) {
    String pop3Host = request.getParameter(Configuration.APP_MAIL_POP3_HOST);
    config.setMailPop3Host(pop3Host);
    if (null != pop3Host && !"".equals(pop3Host)) {
      try {
        // check host name/address
        final InetAddress address = InetAddress.getByName(pop3Host);
        Socket socket = new Socket();
        try {
          socket.connect(new InetSocketAddress(address, 110), 5 * 1000);
          socket.close();
        } catch (IOException e) {
          errors.put(Configuration.APP_MAIL_POP3_HOST, Configuration.APP_MAIL_POP3_HOST + ".connect");
        }
      } catch (UnknownHostException e) {
        errors.put(Configuration.APP_MAIL_POP3_HOST, Configuration.APP_MAIL_POP3_HOST + ".unknown");
      }
    }

    String pop3User = request.getParameter(Configuration.APP_MAIL_POP3_USER);
    config.setMailPop3User(pop3User);
    String pop3Pass = request.getParameter(Configuration.APP_MAIL_POP3_PASSWORD);
    config.setMailPop3Password(pop3Pass);
    String blogPass = request.getParameter(Configuration.APP_MAIL_BLOG_PASSWORD);
    config.setMailBlogPassword(blogPass);
    String pop3Interval = request.getParameter(Configuration.APP_MAIL_POP3_INTERVAL);
    config.setMailPop3Interval(pop3Interval);

    if (config.getMailPop3Host() != null && !"".equals(config.getMailPop3Host())) {
      if (pop3User == null || "".equals(pop3User)) {
        errors.put(Configuration.APP_MAIL_POP3_USER, Configuration.APP_MAIL_POP3_USER);
      }

      if (blogPass == null || "".equals(blogPass) || blogPass.length() < 3) {
        errors.put(Configuration.APP_MAIL_BLOG_PASSWORD, Configuration.APP_MAIL_BLOG_PASSWORD);
      }

      try {
        int interval = Integer.parseInt(pop3Interval);
        if (interval < 5) {
          errors.put(Configuration.APP_MAIL_POP3_INTERVAL, Configuration.APP_MAIL_POP3_INTERVAL);
        }
      } catch (NumberFormatException e) {
        errors.put(Configuration.APP_MAIL_POP3_INTERVAL, Configuration.APP_MAIL_POP3_INTERVAL + ".format");
      }

    }
  }

  private void setupProxy(HttpServletRequest request, Configuration config, Map errors) {
    String autodetect = request.getParameter(Configuration.APP_REAL_AUTODETECT) != null ? "true" : "false";
    config.setRealAutodetect(autodetect);
    if ("false".equals(autodetect)) {
      config.setRealHost(request.getParameter(Configuration.APP_REAL_HOST));
      String portStr = request.getParameter(Configuration.APP_REAL_PORT);
      config.setRealPort(request.getParameter(Configuration.APP_REAL_PORT));
      if (portStr != null && !"".equals(portStr)) {
        try {
          Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
          errors.put(Configuration.APP_REAL_PORT, Configuration.APP_REAL_PORT);
        }
      }
    }
    config.setRealPath(request.getParameter(Configuration.APP_REAL_PATH));
  }

  private void setupDatabase(HttpServletRequest request, Configuration config, Map errors) {
    boolean internalDatabase = request.getParameter("app.jdbc.internal") != null;
    if (internalDatabase) {
      config.setJdbcDriver(config.getDefault(Configuration.APP_JDBC_DRIVER));
      config.setJdbcUrl(config.getDefault(Configuration.APP_JDBC_URL));
      config.setJdbcUser("snipsnap");
      config.setJdbcPassword("snipsnap");
    } else {
      String jdbcDriver = request.getParameter(Configuration.APP_JDBC_DRIVER);
      config.setJdbcDriver(jdbcDriver);
      try {
        Class.forName(jdbcDriver);
      } catch (ClassNotFoundException e) {
        errors.put(Configuration.APP_JDBC_DRIVER, Configuration.APP_JDBC_DRIVER);
      }
      config.setJdbcUrl(request.getParameter(Configuration.APP_JDBC_URL));
      config.setJdbcUser(request.getParameter(Configuration.APP_JDBC_USER));
      config.setJdbcPassword(request.getParameter(Configuration.APP_JDBC_PASSWORD));
    }
  }

  /**
   * Check a path if it is writable. Returns true if one of the parents is writable and
   * false if one of the parents is not writable.
   * @param path the path to check
   * @return whether the path if writable or creatable
   */
  private boolean checkPath(String path) {
    File pathFile = new File(path);
    while (pathFile.getParentFile() != null && !pathFile.exists()) {
      pathFile = pathFile.getParentFile();
    }
    return pathFile.exists() && pathFile.canWrite();
  }

  private void setupExpert(HttpServletRequest request, Configuration config, Map errors) {
    String startSnip = request.getParameter(Configuration.APP_START_SNIP);
    config.setStartSnip(null == startSnip || "".equals(startSnip) ? "start" : startSnip);
    config.setPermCreateSnip(allowDeny(request.getParameter(Configuration.APP_PERM_CREATESNIP)));
    String indexPath = request.getParameter(Configuration.APP_INDEX_PATH);
    if (indexPath != null && indexPath.length() > 0) {
      config.setIndexPath(indexPath);
      indexPath = config.getIndexPath();
      if (!checkPath(indexPath)) {
        errors.put(Configuration.APP_INDEX_PATH, Configuration.APP_INDEX_PATH);
      }
    }
    String filePath = request.getParameter(Configuration.APP_FILE_PATH);
    if (filePath != null && filePath.length() > 0) {
      config.setFilePath(filePath);
      filePath = config.getFilePath();
      if (!checkPath(filePath)) {
        errors.put(Configuration.APP_FILE_PATH, Configuration.APP_FILE_PATH);
      }
    }
    String logger = request.getParameter(Configuration.APP_LOGGER);
    config.setLogger(logger != null && logger.length() > 0 ? logger : "org.radeox.util.logging.NullLogger");
    String cache = request.getParameter(Configuration.APP_CACHE);
    config.setCache(cache != null && cache.length() > 0 ? cache : "full");
    String encoding = request.getParameter(Configuration.APP_ENCODING);
    config.setEncoding(encoding != null && encoding.length() > 0 ? encoding : "UTF-8");
  }
}