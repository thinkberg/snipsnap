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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class SnipTag extends ConditionalTagSupport {
  Snip snip = null;
  String id = null;

  private String name = null;

  public int doStartTag() throws JspException {
    try {
      String snipName = (String) ExpressionEvaluatorManager.evaluate("load", name, Object.class, this, pageContext);
      snip = SnipSpace.getInstance().load(snipName);
    } catch (JspException e) {
      System.err.println("unable to evaluate expression: " + e);
    }
    return super.doStartTag();
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLoad(String name) {
    this.name = name;
  }

  protected boolean condition() throws JspTagException {
    if (id != null && snip != null) {
      pageContext.setAttribute(id, snip);
      return false;
    }
    return true;
  }
}
