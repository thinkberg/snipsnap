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
import org.radeox.util.logging.Logger;
import org.snipsnap.semanticweb.DublinCore;
import org.snipsnap.snip.Snip;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class DublinCoreTag extends TagSupport {
  Snip snip = null;
  String format = null;

  private String capitalize(String s) {
    char chars[] = s.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  public int doStartTag() throws JspException {
    if (null != snip) {
      Map dublinCore = DublinCore.generate(snip);
      try {
        JspWriter out = pageContext.getOut();

        if ("xml".equals(format)) {
          Iterator iterator = dublinCore.keySet().iterator();
          while (iterator.hasNext()) {
            String name = (String) iterator.next();
            String value = (String) dublinCore.get(name);
            out.print("<dc:");
            out.print(name.toLowerCase());
            out.print(">");
            out.print(value);
            out.print("</dc:");
            out.print(name);
            out.println(">");
          }
        } else {
          out.println("<link rel=\"schema.DC\" href=\"http://purl.org/DC/elements/1.1/\"/>");
          Iterator iterator = dublinCore.keySet().iterator();
          while (iterator.hasNext()) {
            String name = (String) iterator.next();
            String value = (String) dublinCore.get(name);
            out.print("<meta name=\"DC.");
            out.print(capitalize(name));
            out.print("\" content=\"");
            out.print(value);
            out.println("\"/>");
          }
        }
      } catch (IOException e) {
        Logger.warn("doStartTag in DublinCore", e);
      }
    }
    return super.doStartTag();
  }


  public void setFormat(String format) {
    this.format = format;
  }

  public void setSnip(String snip) {
    try {
      this.snip = (Snip) ExpressionEvaluatorManager.evaluate("snip", snip, Snip.class, this, pageContext);
    } catch (JspException e) {
      Logger.warn("unable to evaluate expression", e);
    }
  }
}
