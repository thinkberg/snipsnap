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

import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import org.snipsnap.container.Components;
import snipsnap.api.snip.SnipSpace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ManageSearchEngine implements SetupHandler {
  public String getName() {
    return "search";
  }

  private Map indexerThreads = new HashMap();

  public Map setup(HttpServletRequest request, HttpServletResponse response, snipsnap.api.config.Configuration config, Map errors) {
    if (request.getParameter("reset") != null) {
      final String appOid = (String) Application.get().getObject(Application.OID);
      Thread indexerThread = (Thread) indexerThreads.get(appOid);
      if (indexerThread != null && indexerThread.isAlive()) {
        if (request.getSession().getAttribute("running") == null) {
          request.getSession().setAttribute("running", new HashMap());
        }
        return errors;
      } else if (indexerThread != null) {
        request.getSession().removeAttribute("running");
        indexerThreads.remove(appOid);
        indexerThread = null;
      }
      indexerThread = new Thread() {
        public void run() {
          snipsnap.api.app.Application.get().storeObject(snipsnap.api.app.Application.OID, appOid);
          ((SnipSpace) Components.getComponent(SnipSpace.class)).reIndex();
        }
      };
      indexerThread.start();
      request.getSession().setAttribute("indexerThread", indexerThread);
      request.getSession().setAttribute("running", new HashMap());
    }

    return errors;
  }
}
