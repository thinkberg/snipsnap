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
package org.snipsnap.snip.filter;

import org.apache.oro.text.regex.MatchResult;
import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.filter.regex.RegexTokenFilter;
import org.snipsnap.snip.filter.macro.context.FilterContext;

import java.util.HashMap;
import java.util.Map;

/*
 * LinkFilter finds [text] in its input and transforms this
 * to <link name="text">
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class ParamFilter extends RegexTokenFilter {
  private Map params;

  public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
    // String[] params = null;
    // String content = null;
    Map param;

    if (params == null) {
      param = Application.get().getParameters();
    } else {
      param = params;
    }

    String name = result.group(1);
    if (param.containsKey(name)) {
      Object value = param.get(name);
      if (value instanceof String[]) {
        buffer.append(((String[]) value)[0]);
      } else {
        buffer.append(value);
      }
    } else {
      buffer.append("<");
      buffer.append(name);
      buffer.append(">");
    }
  }

  public ParamFilter(String[] paramArray) {
    this();
    params = new HashMap();
    int size = paramArray.length;
    for (int i = 0; i < size; i++) {
      params.put("" + (i + 1), paramArray[i]);
    }
  }

  public ParamFilter() {
    super("\\{\\$([^}]*)\\}", SINGLELINE);
  }

  public ParamFilter(Map params) {
    this();
    this.params = params;
  };
}