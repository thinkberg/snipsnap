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
package org.snipsnap.net;

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.User;
import org.snipsnap.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.util.Iterator;

/**
 * Store an iCalendar.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class iCalServlet extends HttpServlet {
  private final static int WD_CREATED = 201;
  private final static int WD_BAD_REQUEST = 400;
  private final static int WD_UNAUTHORIZED = 401;

  public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession(true);
    UserManager um = UserManager.getInstance();

    // make sure the user is authorized
    String auth = request.getHeader("Authorization");
    String login = "", password = "";

    if(auth != null) {
      auth = new String(Base64.decode(auth.substring(auth.indexOf(' ')+1)));
      login = auth.substring(0, auth.indexOf(':'));
      password = auth.substring(auth.indexOf(':')+1);
    }

    User user = um.authenticate(login, password);
    if (user == null) {
      response.setHeader("WWW-Authenticate", "Basic realm=\"SnipSnap\"");
      response.setStatus(WD_UNAUTHORIZED);
      return;
    } else {
      Application.get().setUser(user);
    }

    String method = request.getMethod();
    String pathInfo = request.getPathInfo();

    String name = user.getLogin();
    String file = pathInfo.substring(1);
 /*   try {
      int slashIdx = pathInfo.indexOf('/', 1);
      if (slashIdx > -1) {
        name = pathInfo.substring(1, slashIdx);
        file = pathInfo.substring(slashIdx + 1);
      }
    } catch (Exception e) {
      // ignore and let the if below handle
    }
 */

    System.err.println("iCalServlet: " + method + "(" + user.getLogin() + "," + file + ")");

    // check that we have a name and a file
    if (null == name || null == file) {
      response.setStatus(WD_BAD_REQUEST);
      return;
    }

    // dispatch to methods
    if ("DELETE".equals(method)) {
      delete(name, file);
    } else if ("PUT".equals(method)) {
      put(name, file, request, response);
    } else if("GET".equals(method)) {
      get(name, file, request, response);
    }


    response.setStatus(WD_CREATED);
  }

  protected void delete(String name, String file) {
    SnipSpace space = SnipSpace.getInstance();
    if (space.exists(name)) {
      Snip userSnip = space.load(name);
      Iterator it = userSnip.getChildren().iterator();
      while (it.hasNext()) {
        Snip snip = (Snip) it.next();
        if (snip.getName().equals("calendar-"+name+"-"+file)) {
          space.remove(snip);
        }
      }
    }
  }

  StringBuffer content = new StringBuffer();

  protected void put(String name, String file,
                     HttpServletRequest request, HttpServletResponse response) throws IOException {
    SnipSpace space = SnipSpace.getInstance();
    if (space.exists(name)) {
      Snip userSnip = space.load(name);

      BufferedReader r = request.getReader();
      content.setLength(0);
      char buffer[] = new char[1024];
      int l = 0;
      while ((l = r.read(buffer)) != -1) {
        content.append(buffer, 0, l);
      }
      Snip snip = space.create("calendar-"+name+"-"+file, content.toString());
      userSnip.addSnip(snip);
    }
  }

  protected void get(String name, String file,
                     HttpServletRequest request, HttpServletResponse response) throws IOException {
    SnipSpace space = SnipSpace.getInstance();
    if (space.exists(name)) {
      PrintWriter w = response.getWriter();
      Snip userSnip = space.load(name);
      Iterator it = userSnip.getChildren().iterator();
      while (it.hasNext()) {
        Snip snip = (Snip) it.next();
        if (snip.getName().equals("calendar-"+name+"-"+file)) {
          String content = snip.getContent();
          response.setContentLength(content.length());
          response.setContentType("application/octet-stream");
          w.print(content);
          w.flush();
          w.close();
          return;
        }
      }
    }
  }


}
