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
/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package org.snipsnap.snip.filter.macro;

import org.snipsnap.snip.Snip;

import java.io.IOException;
import java.io.Writer;


public class ApiMacro extends Macro {
  public String getName() {
    return "api";
  }

  public String getDescription() {
    return "Generates links to Java or Ruby API documentation, e.g. \\{api:java.lang.String\\} generates " +
    " {api:java.lang.String}";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {
    String mode;
    String klass;

    if (params.getLength() == 1) {
      mode = "java";
      klass = params.get("0");
    } else if (params.getLength() == 2) {
      mode = params.get("1").toLowerCase();
      klass = params.get("0");
    } else {
      throw new IllegalArgumentException("api macro needs one or two paramaters");
    }

    StringBuffer url = new StringBuffer();

    if ("java".equals(mode)) {
      // Transform java.lang.StringBuffer to
      // http://java.sun.com/j2se/1.4/docs/api/java/lang/StringBuffer.html
      url.append("http://java.sun.com/j2se/1.4/docs/api/");
      url.append(klass.replace('.', '/'));
      url.append(".html");

    } else if ("ruby".equals(mode)) {
      url.append("http://www.rubycentral.com/book/ref_c_");
      url.append(klass.toLowerCase());
      url.append(".html");
    }
    writer.write("<a href=\"");
    writer.write(url.toString());
    writer.write("\">");
    writer.write(klass);
    writer.write("</a>");
    return;
  }
}
