/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
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
import org.snipsnap.jdbc.IntHolder;
import org.snipsnap.net.filter.MultipartWrapper;
import org.snipsnap.snip.XMLSnipImport;
import org.snipsnap.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DatabaseImport implements SetupHandler {
  private HashMap workerThreads = new HashMap();

  public String getName() {
    return "import";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String appOid = (String) Application.get().getObject(Application.OID);
    ImportThread workerThread = (ImportThread) workerThreads.get(appOid);
    if (workerThread != null && workerThread.isAlive()) {
      setRunning(workerThread, request.getSession());
      return errors;
    } else if (workerThread != null) {
      workerThreads.remove(appOid);
      request.getSession().removeAttribute("running");
      if(workerThread.getErrors() != null) {
        errors.putAll(workerThread.getErrors());
      } else {
        errors.put("message", "import.okay");
      }
      return errors;
    }

    if(request instanceof MultipartWrapper) {
      MultipartWrapper req = (MultipartWrapper) request;

      boolean overwrite = request.getParameter("import.overwrite") != null;
      String importTypes[] = request.getParameterValues("import.types");
      req.setAttribute("importOverwrite", overwrite ? "true" : "false");
      req.setAttribute("importTypes", importTypes);

      int importMask = overwrite ? XMLSnipImport.OVERWRITE : 0;
      for (int i = 0; importTypes != null && i < importTypes.length; i++) {
        if ("users".equals(importTypes[i])) {
          importMask = importMask | XMLSnipImport.IMPORT_USERS;
          req.setAttribute("importTypeUsers", "true");
        }
        if ("snips".equals(importTypes[i])) {
          importMask = importMask | XMLSnipImport.IMPORT_SNIPS;
          req.setAttribute("importTypeSnips", "true");
        }
      }

      try {
        InputStream is = new BufferedInputStream(req.getFileInputStream("import.file"));
        if (importMask == XMLSnipImport.OVERWRITE) {
          errors.put("import.types", "import.types");
        } else if (is != null && is.available() > 0) {
          ImportThread importThread = new ImportThread(is, importMask);
          importThread.start();
          workerThreads.put(appOid, importThread);
          setRunning(importThread, req.getSession());
        } else {
          errors.put("import.file", "import.file");
        }
      } catch (IOException e) {
        errors.put("message", "import.failed");
      }
    }
    return errors;
  }

  private void setRunning(ImportThread workerThread, HttpSession session) {
    Map statusMap = (Map) session.getAttribute("running");
    if (null == statusMap) {
      statusMap = new HashMap();
    }
    statusMap.put("max", new Long(workerThread.getMax()));
    statusMap.put("current", new Long(workerThread.getCurrent()));
    statusMap.put("import", "true");
    session.setAttribute("running", statusMap);
  }

  class ImportThread extends Thread {
    private User user;
    private String appOid;
    private InputStream file;
    private int mask;

    private IntHolder maxValue;
    private IntHolder status;

    private Map errors = null;

    public Map getErrors() {
      return errors;
    }

    public int getMax() {
      if(null == maxValue) {
        return 0;
      }
      return maxValue.getValue();
    }

    public int getCurrent() {
      if (null == status) {
        return 0;
      }
      return status.getValue();
    }

    public ImportThread(InputStream file, int mask) {
      this.file = file;
      this.mask = mask;
      this.appOid = (String)Application.get().getObject(Application.OID);
      this.user = Application.get().getUser();
    }

    public void run() {
      Application.get().storeObject(Application.OID, appOid);
      Application.get().setUser(user);

      Configuration config = Application.get().getConfiguration();

      System.err.println("ConfigureServlet: Disabling weblogs ping and jabber notification ...");
      String ping = config.get(Configuration.APP_PERM_WEBLOGSPING);
      String noty = config.get(Configuration.APP_PERM_NOTIFICATION);
      config.set(Configuration.APP_PERM_WEBLOGSPING, "deny");
      config.set(Configuration.APP_PERM_NOTIFICATION, "deny");

      maxValue = XMLSnipImport.getMax();
      status = XMLSnipImport.getStatus();

      try {
        XMLSnipImport.load(file, mask);
      } catch (Exception e) {
        System.err.println("ConfigurServlet.importDatabase: unable to import snips: " + e);
        e.printStackTrace();
        errors = new HashMap();
        errors.put("message", "import.failed");
      }

      System.err.println("ConfigureServlet: Resetting weblogs ping and jabber notification to config settings ...");
      config.set(Configuration.APP_PERM_WEBLOGSPING, ping);
      config.set(Configuration.APP_PERM_NOTIFICATION, noty);
    }
  }
}

