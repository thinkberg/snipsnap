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

import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.XMLSnipExport;
import org.snipsnap.user.UserManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DatabaseExport implements SetupHandler {
  public String getName() {
    return "export";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String output = request.getParameter("export.file");
    request.setAttribute("exportFile", output);
    String exportTypes[] = request.getParameterValues("export.types");
    request.setAttribute("exportTypes", exportTypes);

    UserManager um = (UserManager) Components.getComponent(UserManager.class);
    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);

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
        out = new FileOutputStream(outFile);
      } else if ("download".equals(output)) {
        response.setContentType("text/xml");
        out = response.getOutputStream();
      } else {
        errors.put("message", "export.failed");
        return errors;
      }

      XMLSnipExport.store(out, snips, users, exportIgnore, config.getFilePath());
      if ("webinf".equals(output)) {
        errors.put("message", "export.okay");
      }
    } catch (IOException e) {
      errors.put("message", "export.failed");
    }

    if(errors.size() == 0) {
      return null;
    }
    return errors;
  }
}