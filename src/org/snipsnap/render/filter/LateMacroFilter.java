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
 * Class that finds snippets like
 * {link|neotis|http://www.neotis.de} ---> <elink ....>
 * {neotis} -> include neotis wiki
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package org.snipsnap.render.filter;

import org.apache.oro.text.regex.MatchResult;
import org.snipsnap.serialization.StringBufferWriter;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.render.macro.Macro;
import org.snipsnap.render.macro.parameter.MacroParameter;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.render.macro.WeblogMacro;
import org.snipsnap.render.filter.context.SnipFilterContext;
import org.snipsnap.render.filter.context.FilterContext;
import org.snipsnap.render.filter.regex.RegexTokenFilter;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class LateMacroFilter extends RegexTokenFilter {

  private static LateMacroFilter instance;

  private Map macros;
  private static Object monitor = new Object();

  public LateMacroFilter() {
    super("\\{([^:}]+):?(.*?)\\}(.*?)\\{(\\1)\\}", SINGLELINE);
    addRegex("\\{([^:}]+):?(.*?)\\}", "", MULTILINE);

    macros = new HashMap();

    add(new WeblogMacro());
  }

  public static Filter getInstance() {
    synchronized (monitor) {
      if (null == instance) {
        instance = new LateMacroFilter();
      }
    }
    return instance;
  }

  public void add(Macro macro) {
    macros.put(macro.getName(), macro);
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
    Snip snip = ((SnipFilterContext) context).getSnip();

    String command = result.group(1);

// System.out.println("Parameter block:" + Application.get().getParameters() );

// {$peng} are variables not macros.
    if (!command.startsWith("$")) {
//      for (int i=0; i<result.groups(); i++) {
//        System.err.println(i+" "+result.group(i));
//      }

      MacroParameter mParams = new SnipMacroParameter(snip);
// {tag} ... {tag}
      if (result.group(1).equals(result.group(result.groups() - 1))) {
// {tag:1|2} ... {tag}
        if (!"".equals(result.group(2))) {
          mParams.setParams(result.group(2));
        }
        mParams.setContent(result.group(3));
      } else {
// {tag}
        if (result.groups() > 1) {
// {tag:1|2}
          mParams.setParams(result.group(2));
        }
      }

// @DANGER: recursive calls may replace macros in included source code
      try {
        if (macros.containsKey(command)) {
          Macro macro = (Macro) macros.get(command);
// recursively filter macros within macros
          if (null != mParams.getContent()) {
            mParams.setContent(filter(mParams.getContent(), context));
          }
          Writer writer = new StringBufferWriter(buffer);
          macro.execute(writer, mParams);
        } else if (command.startsWith("!")) {
// @TODO including of other snips
          Snip includeSnip = SnipSpace.getInstance().load(command.substring(1));
          if (null != includeSnip) {
            String included = includeSnip.getContent();
            //Filter paramFilter = new ParamFilter(params);
            //buffer.append(paramFilter.filter(included, null));
          } else {
            buffer.append(command.substring(1) + " not found.");
          }
          return;
        } else {
          buffer.append(result.group(0));
          return;
        }
      } catch (IllegalArgumentException e) {
        buffer.append("<div class=\"error\">" + command + ": " + e.getMessage() + "</div>");
      } catch (Exception e) {
        System.err.println("unable to format macro: " + result.group(1));
        buffer.append("<div class=\"error\">" + command + "</div>");
        e.printStackTrace();
        return;
      }
    } else {
      buffer.append("<");
      buffer.append(command.substring(1));
      buffer.append(">");
    }
  }
}
