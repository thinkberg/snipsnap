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
import com.neotis.snip.filter.Filter;
import com.neotis.snip.filter.JavaCodeFilter;
import com.neotis.snip.filter.SqlCodeFilter;
import com.neotis.snip.filter.XmlCodeFilter;

import java.util.HashMap;
import java.util.Map;

public class CodeMacro extends Preserved {
  private Map filters;

  public CodeMacro() {
    filters = new HashMap();
    filters.put("xml", new XmlCodeFilter());
    filters.put("java", new JavaCodeFilter());
    filters.put("sql", new SqlCodeFilter());

    addSpecial("[", "&#x005b;");
    addSpecial("]", "&#x005d;");
    addSpecial("{", "&#x007b;");
    addSpecial("}", "&#x007d;");
  }

  public String getName() {
    return "code";
  }

  public void execute(StringBuffer buffer, String[] params, String content, Snip snip) throws IllegalArgumentException {
    Filter filter = null;

    if (params == null || !filters.containsKey(params[0])) {
      filter = (Filter) filters.get("java");
    } else {
      filter = (Filter) filters.get(params[0]);
    }
    String result = filter.filter(content, snip);

    buffer.append("<div class=\"code\"><pre>");
    buffer.append(replace(result.trim()));
    buffer.append("</pre></div>");
    return;
  }
}
