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

package com.neotis.snip.filter.macro;

import com.neotis.snip.Snip;


public class ApiMacro extends Macro {
  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    String mode;

    if (params.length == 1) {
      mode = "java";
    } else if (params.length == 2) {
      mode = params[1].toLowerCase();
    } else {
      throw new IllegalArgumentException("api macro needs one or two paramaters");
    }

    StringBuffer url = new StringBuffer();

    if ("java".equals(mode)) {
      // Transform java.lang.StringBuffer to
      // http://java.sun.com/j2se/1.4/docs/api/java/lang/StringBuffer.html
      url.append("http://java.sun.com/j2se/1.4/docs/api/");
      url.append(params[0].replace('.', '/'));
      url.append(".html");

    } else if ("ruby".equals(mode)) {
      url.append("http://www.rubycentral.com/book/ref_c_");
      url.append(params[0].toLowerCase());
      url.append(".html");
    }
    return "<a href=\"" + params[0] + "\"<a href=\"" + url.toString() + "\">" + params[0] + "</a>";
  }
}
