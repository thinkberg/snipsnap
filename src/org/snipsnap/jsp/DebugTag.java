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

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.radeox.util.logging.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class DebugTag extends TagSupport {
  Snip snip = null;

  public int doStartTag() throws JspException {
    JspWriter out = pageContext.getOut();
    Application app = Application.get();
    List debug = app.getLog();
    app.clearLog();
    if (!debug.isEmpty()) {
      Iterator iterator = debug.iterator();
      try {
        out.println("<div class=\"debug\">");
        out.println("<h1 class=\"debug-title\">Debug Log:</h1>");
        while (iterator.hasNext()) {
          String s = (String) iterator.next();
          out.print("<div class=\"debug-entry\">");
          out.print(s);
          out.print("</div>");
        }
        out.println("</div>");
      } catch (IOException e) {
        Logger.warn("unable print to JSP writer", e);
      }
    }
    return super.doStartTag();
  }
}
