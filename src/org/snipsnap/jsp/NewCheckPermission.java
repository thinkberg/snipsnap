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
package org.snipsnap.jsp;

import gabriel.Permission;
import gabriel.components.context.OwnerAccessContext;
import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.security.AccessController;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpaceFactory;
import snipsnap.api.user.User;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class NewCheckPermission extends ConditionalTagSupport {
  protected snipsnap.api.snip.Snip snip;
  protected Permission permission;
  protected boolean invertCheck = false;

  /**
   * Set the snip for the context of permission checking.
   *
   * @param snip String with the name of the snip
   */
  public void setName(String snip) {
    try {
      String snipName = (String) ExpressionEvaluatorManager.evaluate("snip", snip, String.class, this, pageContext);
      this.snip = snipsnap.api.snip.SnipSpaceFactory.getInstance().load(snipName);
    } catch (JspException e) {
      Logger.warn("unable to evaluate expression", e);
    }
  }

  /**
    * Set the snip for the context of permission checking.
    *
    * @param snip Snip to check permissions
    */
   public void setContext(String snip) {
    try {
      this.snip = (Snip) ExpressionEvaluatorManager.evaluate("snip", snip, Snip.class, this, pageContext);
    } catch (JspException e) {
      Logger.warn("unable to evaluate expression", e);
    }
  }


  public void setInvert(Boolean value) {
    invertCheck = value.booleanValue();
  }

  public void setPermission(String permission) {
    this.permission = new Permission(permission);
  }

  protected boolean condition() throws JspTagException {
    AccessController controller = (AccessController) Components.getComponent(AccessController.class);

    Application app = snipsnap.api.app.Application.get();
    User user = app.getUser();
    boolean isTrue = false;
    isTrue = controller.checkPermission(user, permission, new OwnerAccessContext(snip));
    // invert result if necessary
    return (invertCheck ? !isTrue : isTrue);
  }
}
