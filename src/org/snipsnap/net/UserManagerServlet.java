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
package com.neotis.net;

import com.neotis.user.Roles;
import com.neotis.user.User;
import com.neotis.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Servlet used for interfacing to external user management. Sets the current user manager
 * into the session as usermanager:/contextPath.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class UserManagerServlet extends HttpServlet {

  public final static String UPDATE = "update";
  public final static String REMOVE = "remove";

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    // get user manager and store in session
    UserManager um = UserManager.getInstance();
    request.setAttribute("usermanager", um);

    // get user (if possible) and store in session
    String login = request.getParameter("login");
    User user = um.load(login);
    request.setAttribute("user", user);

    if (request.getAttribute("prepare") == null && request.getParameter("ok") != null) {
      String command = request.getParameter("command");
      if (UPDATE.equals(command)) {
        update(request);
      } else if (REMOVE.equals(command)) {
        um.remove(user);
        request.removeAttribute("user");
      } else {
        System.err.println("UserManagerServlet: unknown command '" + command + "'");
      }
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  private final static String ERR_UNKNOWN_ROLES = "Acceptable roles include: ";
  private final static String ERR_WRONG_PASSWORD = "Passwords do not match!";
  private final static String OK_USER_UPDATED = "User information has been updated.";

  /**
   * Update a user, check validity and equality of the input.
   */
  private void update(HttpServletRequest request) {
    UserManager um = UserManager.getInstance();
    String login = request.getParameter("login");
    User user = um.load(login);

    String email = request.getParameter("email");
    String nPass = request.getParameter("password.new");
    String nPass2 = request.getParameter("password2.new");
    String status = request.getParameter("status");
    String roles[] = request.getParameterValues("roles");

    boolean modified = false;
    Map errors = new HashMap();
    if (!user.getEmail().equals(email)) {
      modified = true;
      user.setEmail(email);
    }

    if (!user.getStatus().equals(status)) {
      modified = true;
      user.setStatus(status);
    }

    Roles newRoles = new Roles(parseRoles(roles));
    if (!user.getRoles().equals(newRoles)) {
      if (!newRoles.getRoleSet().isEmpty() && !Roles.allRoles().containsAll(newRoles.getRoleSet())) {
        errors.put("roles", ERR_UNKNOWN_ROLES + Roles.allRoles());
      } else {
        modified = true;
        user.setRoles(newRoles);
      }
    }


    if (nPass != null && nPass.length() > 0) {
      if (nPass.equals(nPass2)) {
        modified = true;
        user.setPasswd(nPass);
      } else {
        errors.put("password", ERR_WRONG_PASSWORD);
      }
    }

    if (modified) {
      um.store(user);
      errors.put("", OK_USER_UPDATED);
    }
    request.setAttribute("user", user);
    request.setAttribute("errors", errors);
  }

  private Set parseRoles(String roles[]) {
    Set list = new HashSet();
    for (int i = 0; roles != null && i < roles.length; i++) {
      list.add(roles[i]);
    }
    return list;
  }
}
