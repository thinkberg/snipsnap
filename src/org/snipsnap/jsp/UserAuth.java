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
import com.neotis.snip.Snip;
import com.neotis.user.Security;
import com.neotis.user.User;
import com.neotis.user.Roles;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import java.util.*;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;


public class UserAuth extends ConditionalTagSupport {
  protected Roles roles;
  protected Snip snip;

  protected boolean invertCheck = false;

  public void setSnip(String snip) {
    try {
      this.snip = (Snip)ExpressionEvaluatorManager.evaluate("snip", snip, Snip.class, this, pageContext);
    } catch (JspException e) {
      System.err.println("unable to evaluate expression: "+e);
    }
  }

  public void setRoles(String roles) {
    StringTokenizer tok = new StringTokenizer(roles, ":,");
    this.roles = new Roles();
    while (tok.hasMoreTokens()) {
      String token = tok.nextToken();
      this.roles.add(token);
    }
  }


  public void setInvert(Boolean value) {
    invertCheck = value.booleanValue();
  }

  protected boolean condition() throws JspTagException {
    Application app = (Application) pageContext.findAttribute("app");
    User user = app.getUser();
    if (snip != null) {
      boolean isTrue = Security.hasRoles(user, snip, roles);
      return (invertCheck ? !isTrue : isTrue);
    } else {
      boolean isTrue = Security.hasRoles(user, roles);
      return (invertCheck ? !isTrue : isTrue);
    }
  }
}
