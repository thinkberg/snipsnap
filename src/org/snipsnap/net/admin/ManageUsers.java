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
import org.snipsnap.user.UserManager;
import org.snipsnap.user.User;
import org.snipsnap.user.Roles;
import org.snipsnap.container.Components;
import org.snipsnap.snip.HomePage;
import org.snipsnap.app.Application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class ManageUsers implements SetupHandler {
  public String getName() {
    return "users";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    UserManager um = (UserManager) Components.getComponent(UserManager.class);
    if (request.getParameter("remove") != null) {
      User user = um.load(request.getParameter("remove"));
      if(user != null) {
        um.remove(user);
      }
      return errors;
    } else if (request.getParameter("edit") != null) {
      User user = um.load(request.getParameter("login"));
      request.setAttribute("editUser", user);
      request.setAttribute("edit", "true");
      if(null == user) {
        request.setAttribute("editUser", new User());
        request.setAttribute("create", "true");
      }
    } else if (request.getParameter("save") != null || request.getParameter("create") != null) {
      request.setAttribute("edit", "true");
      String login = request.getParameter("config.users.login");
      User user = um.load(login);
      if(request.getParameter("create") != null) {
        request.setAttribute("create", request.getParameter("create"));

        if(user != null || null == login) {
          errors.put("users.login", "users.login");
        } else {
          User tmp = new User(login, "", "");
          if(update(request, errors, tmp)) {
            user = um.create(login, tmp.getPasswd(), tmp.getEmail());
            update(request, errors, user);
            if(errors.size() == 0) {
              User currUser = Application.get().getUser();
              Application.get().setUser(user);
              HomePage.create(user.getLogin());
              Application.get().setUser(currUser);
            }
          }
        }
      } else if(null == user) {
        errors.put("users.login", "users.login");
        return errors;
      }

      if (update(request, errors, user)) {
        um.store(user);
      }
      request.setAttribute("editUser", user);
      if(errors.size() == 0) {
        request.removeAttribute("edit");
      }
    }

    return errors;
  }

  /**
   * Update a user, check validity and equality of the input.
   */
  private boolean update(HttpServletRequest request, Map errors, User user) {

    String email = request.getParameter("config.users.email");
    String nPass = request.getParameter("config.users.password");
    String nPass2 = request.getParameter("config.users.password.vrfy");
    String status = request.getParameter("config.users.status");
    String roles[] = request.getParameterValues("config.users.roles");

    boolean modified = false;
    if ((user.getEmail() == null && email != null) ||
      (user.getEmail() != null && !user.getEmail().equals(email))) {
      modified = true;
      user.setEmail(email);
    }

    if ((user.getStatus() == null && status != null) ||
      (user.getStatus() != null && !user.getStatus().equals(status))) {
      modified = true;
      user.setStatus(status);
    }

    Roles newRoles = new Roles(parseRoles(roles));
    if (!user.getRoles().equals(newRoles)) {
      if (!newRoles.getRoleSet().isEmpty() && !Roles.allRoles().containsAll(newRoles.getRoleSet())) {
        errors.put("users.roles", "users.roles");
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
        errors.put("users.password", "users.password");
      }
    }

    return modified;
  }

  private Set parseRoles(String roles[]) {
    Set list = new HashSet();
    for (int i = 0; roles != null && i < roles.length; i++) {
      list.add(roles[i]);
    }
    return list;
  }
}
