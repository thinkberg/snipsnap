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
import org.snipsnap.snip.Links;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.util.ColorRange;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import java.util.Iterator;
import java.io.IOException;

public class SnipLinkTag extends TagSupport {
  Snip snip = null;
  String start = "#ffffff";
  String end = "#b0b0b0";
  int width = 4;

  public int doStartTag() throws JspException {
    JspWriter out = pageContext.getOut();
    Links snipLinks = snip.getAccess().getSnipLinks();
    Iterator iterator = snipLinks.iterator();

    int size = snipLinks.getSize();
    int percentPerCell = 100/width;
    ColorRange cr = new ColorRange(start, end, Math.max(size <= 20 ? size : 20, 8));

    try {
      int i = 0;
      out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
      out.println("<caption>see also:</caption>");
      out.println("<tr>");
      while (iterator.hasNext() && i <= 20) {
        if (i % width == 0 && i!= 0 ) {
          out.print("</tr><tr>");
        }
        String url = (String) iterator.next();
        out.print("<td bgcolor=\"");
        out.print(cr.getColor(i++));
        out.print("\" width=\"");
        out.print(percentPerCell);
        out.print("%\">");
        out.print(SnipLink.createLink(url, SnipLink.cutLength(url, 25)));
        // out.print(" - " + snipLinks.getIntCount(url));
        out.println("</td>");
      }
      out.println("</tr></table>");
    } catch (IOException e) {
      System.err.println("unable print to JSP writer: " + e);
    }
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
      System.err.println("unable to evaluate expression: " + e);
    }
  }
}