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
import org.snipsnap.snip.Links;
import org.snipsnap.snip.Snip;
import org.snipsnap.render.filter.links.SnipLinks;
import org.radeox.util.logging.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.TagSupport;

public class SnipLinkTag extends TagSupport {
  Snip snip = null;
  String start = "#ffffff";
  String end = "#b0b0b0";
  int width = 4;

  public int doStartTag() throws JspException {
    if (snip == null) {
      return BodyTag.SKIP_BODY;
    }

    JspWriter out = pageContext.getOut();
    Links snipLinks = snip.getAccess().getSnipLinks();
    SnipLinks.appendTo(out, snip.getAccess().getSnipLinks(), this.width, this.start, this.end);

    return super.doStartTag();
  }

  public void setStart(String start) {
    this.start = start;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setSnip(String snip) {
    try {
      this.snip = (Snip) ExpressionEvaluatorManager.evaluate("snip", snip, Snip.class, this, pageContext);
    } catch (JspException e) {
      Logger.warn("unable to evaluate expression", e);
    }
  }
}