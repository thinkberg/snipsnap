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

import snipsnap.api.config.Configuration;
import snipsnap.api.container.Components;
import snipsnap.api.snip.SnipSpace;
import org.snipsnap.snip.XMLSnipExport;
import org.snipsnap.user.UserManager;
import snipsnap.api.app.Application;
import org.snipsnap.jdbc.IntHolder;
import org.radeox.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DatabaseExport implements SetupHandler {
  private HashMap workerThreads = new HashMap();

  public String getName() {
    return "export";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String appOid = (String) snipsnap.api.app.Application.get().getObject(snipsnap.api.app.Application.OID);
    ExportThread workerThread = (ExportThread) workerThreads.get(appOid);
    if (workerThread != null && workerThread.isAlive()) {
      setRunning(workerThread, request.getSession());
      return errors;
    } else if(workerThread != null) {
      workerThreads.remove(appOid);
      request.getSession().removeAttribute("running");
      errors.put("message", "export.okay");
      return errors;
    }

    String output = request.getParameter("export.file");
    request.setAttribute("exportFile", output);
    String exportTypes[] = request.getParameterValues("export.types");
    request.setAttribute("exportTypes", exportTypes);

    UserManager um = (UserManager) snipsnap.api.container.Components.getComponent(UserManager.class);
    snipsnap.api.snip.SnipSpace space = (SnipSpace) Components.getComponent(snipsnap.api.snip.SnipSpace.class);

    List users = null;
    List snips = null;

    String exportMatch = request.getParameter("export.match");
    if (null == exportMatch) {
      exportMatch = "";
    }
    request.setAttribute("exportMatch", exportMatch);

    String exportIgnore = request.getParameter("export.ignore");
    request.setAttribute("exportIgnore", exportIgnore == null ? "" : exportIgnore);
    if ("".equals(exportIgnore)) {
      exportIgnore = null;
    }

    for (int i = 0; i < exportTypes.length; i++) {
      if ("users".equals(exportTypes[i])) {
        users = um.getAll();
        request.setAttribute("exportTypeUsers", "true");
      }
      if ("snips".equals(exportTypes[i])) {
        if (null != exportMatch && !"".equals(exportMatch)) {
          snips = Arrays.asList(space.match(exportMatch));
        } else {
          snips = space.getAll();
        }
        request.setAttribute("exportTypeSnips", "true");
      }
    }

    if (users == null && snips == null) {
      errors.put("export.types", "export.types");
      return errors;
    }

    OutputStream out = null;
    try {
      if ("webinf".equals(output)) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        File outFile = new File(config.getWebInfDir(),
                                config.getName() + "-" + df.format(new Date()) + ".snip");
        workerThread = new ExportThread(new BufferedOutputStream(new FileOutputStream(outFile)), snips, users, exportIgnore, config.getFilePath());
        workerThread.start();
        workerThreads.put(appOid, workerThread);
        setRunning(workerThread, request.getSession());
        return errors;

      } else if ("download".equals(output)) {
        response.setContentType("text/xml");
        out = response.getOutputStream();
      } else {
        errors.put("message", "export.failed");
        return errors;
      }

      XMLSnipExport.store(new BufferedOutputStream(out), snips, users, exportIgnore, null, config.getFilePath());
    } catch (IOException e) {
      errors.put("message", "export.failed");
    }

    if(errors.size() == 0) {
      return null;
    }
    return errors;
  }

  private void setRunning(ExportThread workerThread, HttpSession session) {
    Map statusMap = (Map) session.getAttribute("running");
    if (null == statusMap) {
      statusMap = new HashMap();
    }
    statusMap.put("max", new Integer(workerThread.getMax()));
    statusMap.put("current", new Integer(workerThread.getCurrent()));
    statusMap.put("export", "true");
    session.setAttribute("running", statusMap);

  }

  class ExportThread extends Thread {
    private OutputStream out;
    private List snips, users;
    private String exportIgnore;
    private File filePath;

    private int maxValue = 0;
    private IntHolder status;

    public int getMax() {
      return maxValue;
    }

    public int getCurrent() {
      if(null == status) {
        return 0;
      }
      return status.getValue();
    }

    public ExportThread(OutputStream out, List snips, List users, String ignore, File filePath) {
      this.out = out;
      this.snips = snips;
      this.users = users;
      this.exportIgnore = ignore;
      this.filePath = filePath;
      maxValue = (snips != null ? snips.size() : 0) + (users != null ? users.size() : 0);
    }

    public void run() {
      status = XMLSnipExport.getStatus();
      XMLSnipExport.store(out, snips, users, exportIgnore, null, filePath);
      try {
        out.close();
      } catch (IOException e) {
        Logger.warn("DatabaseExport: unable to close document", e);
      }
    }
  }
}