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
package org.snipsnap.admin.install;

import org.snipsnap.admin.util.ApplicationCommand;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.util.Checksum;
import org.snipsnap.util.FileUtil;
import org.snipsnap.util.JarUtil;
import org.mortbay.jetty.Server;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * Application configuration servlet.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Update extends HttpServlet {

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    HttpSession session = request.getSession();
    if (session != null && session.getAttribute("admin") != null) {
      String srv = request.getParameter("server");
      String ctx = request.getParameter("context");
      session.setAttribute("server", srv);
      session.setAttribute("context", ctx);

      Map errors = new HashMap();

      if (request.getParameter("cancel") == null) {
        if (request.getParameter("update") != null) {
          ApplicationCommand.execute(srv, ctx, ApplicationCommand.CMD_APPLICATION_REMOVE);
          update(request.getParameterValues("install"), request.getParameterValues("extract"), ctx, errors);
          ApplicationCommand.execute(srv, ctx, ApplicationCommand.CMD_APPLICATION_ADD);
          ApplicationCommand.execute(srv, ctx, ApplicationCommand.CMD_APPLICATION_START);
        } else if (request.getParameter("download") != null) {
          try {
            downloadUpdate();
            session.removeAttribute("SRVchecksum");
          } catch (IOException e) {
            errors.put("download", "The updated web application archive could not be downloaded!");
          }
        }
        prepare(ctx, session, errors);

        session.setAttribute("errors", errors);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/update.jsp");
        if (dispatcher != null) {
          dispatcher.forward(request, response);
          return;
        }
      }

    }
    response.sendRedirect(SnipLink.absoluteLink(request, "/"));
  }

  private void downloadUpdate() throws IOException {
    URL update = new URL("http://snipsnap.org/download/snipsnap-template.war");
    BufferedInputStream in = new BufferedInputStream(update.openStream());
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("./lib/snipsnap-template.war"));
    byte buffer[] = new byte[8192];
    int n = 0;
    while ((n = in.read(buffer)) != -1) {
      out.write(buffer, 0, n);
    }
    in.close();
    out.close();
  }

  private void update(String files[], String extract[], String ctx, Map errors) {
    List install = files != null ? Arrays.asList(files) : new ArrayList();
    List unpack = extract != null ? Arrays.asList(extract) : new ArrayList();
    try {
      JarFile template = new JarFile("./lib/snipsnap-template.war");
      JarUtil.extract(template, new File("./app" + ctx), install, unpack);
      // TODO: hack to read servlets jar before restarting
      JarUtil.checksumJar(new JarFile("./app"+ctx+"/WEB-INF/lib/servlets.jar"));
      JarUtil.checksumJar(template).store(new File("./app" + ctx + "/WEB-INF/CHECKSUMS"));
    } catch (Exception e) {
      errors.put("update", "Unable to update your application, see server.log for details!");
      e.printStackTrace();
    }
  }

  /**
   * Prepare installation procedure by checking for changed files.
   * @param ctx
   * @param session
   * @param errors
   */
  private void prepare(String ctx, HttpSession session, Map errors) {
    try {
      // get checksum from template
      Checksum csTemplate = JarUtil.checksumJar(new JarFile("./lib/snipsnap-template.war"));

      // get server checksums
      Checksum server = (Checksum) session.getAttribute("SRVchecksum");
      if (server == null) {
        try {
          server = new Checksum(new URL("http://snipsnap.org/download/CHECKSUMS"));
          session.setAttribute("SRVchecksum", server);
          session.setAttribute("available", server.compareChanged(csTemplate));
        } catch (IOException e) {
          System.err.println("Updater: unable to check for updates on server: " + e);
        }
      }

      // get checksum from installation procedure
      Checksum csAppInstall = null;
      try {
        csAppInstall = new Checksum(new File("./app" + ctx + "/WEB-INF/CHECKSUMS"));
      } catch (IOException e) {
        errors.put("APPchecksum", "No checksum of installed files found.");
        System.err.println("Updater: no checksum file found for '" + ctx + "'");
      }

      // create checksum of currently installed files, store if no install list was available
      Checksum csAppCurrent = null;
      try {
        csAppCurrent = FileUtil.checksumDirectory(new File("./app" + ctx));
        if (csAppInstall == null) {
          csAppCurrent.store(new File("./app" + ctx + "/WEB-INF/CHECKSUMS"));
        }
      } catch (IOException e) {
        errors.put("APPchecksum", "Unable to create or store checksums for installed filed.");
        System.err.println("Updater: unable to create current checksum for installed application '" + ctx + "'");
      }

      if (csTemplate != null && (csAppInstall != null || csAppCurrent != null)) {
        Set changed = null, unchanged = null, updated = null, installable = null;
        if (csAppInstall != null) {
          session.setAttribute("changed", changed = csAppInstall.compareChanged(csAppCurrent));
          session.setAttribute("unchanged", unchanged = csAppInstall.compareUnchanged(csAppCurrent));
          // get files that are definitely installable
          installable = csTemplate.compareChanged(csAppInstall);
          installable.retainAll(unchanged);
          session.setAttribute("installable", installable);
          // get files that have been updated and changed locally
          updated = csTemplate.compareChanged(csAppInstall);
          updated.retainAll(changed);
          updated.removeAll(installable);
          session.setAttribute("updated", updated);
        } else {
          installable = csTemplate.compareUnchanged(csAppCurrent);
          session.setAttribute("installable", installable);
        }

      }
    } catch (IOException e) {
      errors.put("WARchecksum", "Unable to get checksum for template archive.");
      e.printStackTrace();
      errors.put("WARchecksum", "Unable to get checksum from template web archive!");
    }
  }
}

