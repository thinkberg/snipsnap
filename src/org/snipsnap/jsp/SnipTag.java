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

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class SnipTag extends TagSupport {
  Snip snip = null;

  public int doStartTag() throws JspException {
    if (null != snip) {
      try {
        snip.appendTo(pageContext.getOut());
      } catch (IOException e) {
        System.err.println("SnipTag: unable to write snip xml content: "+snip);
      }
    }
    return super.doStartTag();
  }

  public void setSnip(String snip) {
    try {
      this.snip = (Snip) ExpressionEvaluatorManager.evaluate("snip", snip, Snip.class, this, pageContext);
    } catch (JspException e) {
      System.err.println("unable to evaluate expression: " + e);
    }
  }

  public void setName(String name) {
    try {
      String snipName = (String) ExpressionEvaluatorManager.evaluate("name", name, String.class, this, pageContext);
      if(SnipSpace.getInstance().exists(snipName)) {
        snip = SnipSpace.getInstance().load(snipName);
      }
    } catch (JspException e) {
      System.err.println("unable to evaluate expression: " + e);
    }
  }
}
