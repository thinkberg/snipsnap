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
package com.neotis.jsp;

import com.neotis.app.Application;
import com.neotis.user.Security;
import com.neotis.user.User;

import javax.servlet.jsp.JspTagException;


public class CheckPermission extends UserAuth {
  protected String permission;

  public void setPermission(String permission) {
    this.permission = permission;
  }

  protected boolean condition() throws JspTagException {
    Application app = (Application) pageContext.findAttribute("app");
    User user = app.getUser();
    if (snip != null) {
      boolean isTrue = Security.checkPermission(permission, user, snip) && Security.hasRoles(user, snip, roles);
      // invert result if necessary
      return (invertCheck ? !isTrue : isTrue);
    } else {
      return (invertCheck ? !Security.hasRoles(user, roles) : Security.hasRoles(user, roles));
    }
  }
}
