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

import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.snip.SnipSpace;
import snipsnap.api.snip.SnipSpaceFactory;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import snipsnap.api.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to lock and unlock snips
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class SnipLockServlet extends HttpServlet {
  private final static Roles ALLOWED_ROLES = new Roles("Admin:Editor");

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String name = request.getParameter("name");
    SnipSpace space = snipsnap.api.snip.SnipSpaceFactory.getInstance();
    snipsnap.api.snip.Snip snip = space.load(name);

    User user = Application.get().getUser();
    if (user != null && user.getRoles().containsAny(ALLOWED_ROLES)) {
      String role = Roles.EDITOR;
      if(user.getRoles().contains(Roles.ADMIN)) {
        role = Roles.ADMIN;
      }

      if (request.getParameter("unlock") != null) {
        snip.getPermissions().remove(Permissions.EDIT_SNIP, role);
      } else {
        snip.getPermissions().add(Permissions.EDIT_SNIP, role);
      }
      space.store(snip);
    }
    snipsnap.api.config.Configuration config = Application.get().getConfiguration();
    response.sendRedirect(config.getUrl("/space/" + snipsnap.api.snip.SnipLink.encode(name)));
  }
}