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

package org.snipsnap.render.macro;

import org.snipsnap.render.filter.Filter;
import org.snipsnap.render.macro.code.SourceCodeFormatter;
import org.snipsnap.render.filter.context.FilterContext;
import org.snipsnap.render.filter.context.NullFilterContext;
import org.snipsnap.render.macro.parameter.MacroParameter;
import org.snipsnap.util.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CodeMacro extends Preserved {
  private Map formatters;
  private FilterContext nullContext = new NullFilterContext();

  private String[] paramDescription =
     {"?1: syntax highlighter to use, defaults to java"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public CodeMacro() {
    formatters = new HashMap();

    Iterator formatterIt = Service.providers(SourceCodeFormatter.class);
    while (formatterIt.hasNext()) {
      try {
        SourceCodeFormatter formatter = (SourceCodeFormatter) formatterIt.next();
        formatters.put(formatter.getName(), formatter);
        System.err.println("Loaded code formatter: " + formatter.getName());
      } catch (Exception e) {
        System.err.println("CodeMacro: unable to load code formatter: " + e);
        e.printStackTrace();
      }
    }

    addSpecial('[');
    addSpecial(']');
    addSpecial('{');
    addSpecial('}');
    addSpecial('*');
    addSpecial('-');
    addSpecial('\\');
  }

  public String getName() {
    return "code";
  }

  public String getDescription() {
    return "Displays a chunk of code with syntax highlighting, for example Java, XML and SQL. The none type will " +
        "do nothing and is useful for unknown code types.";
  }


  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    SourceCodeFormatter formatter = null;

    if (params.getLength() == 0 || !formatters.containsKey(params.get("0"))) {
      formatter = (SourceCodeFormatter) formatters.get("java");
    } else {
      formatter = (SourceCodeFormatter) formatters.get(params.get("0"));
    }
    String result = formatter.filter(params.getContent(), nullContext);

    writer.write("<div class=\"code\"><pre>");
    writer.write(replace(result.trim()));
    writer.write("</pre></div>");
    return;
  }
}
