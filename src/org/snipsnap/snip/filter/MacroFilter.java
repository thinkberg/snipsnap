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
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.filter.macro.*;
import org.snipsnap.snip.filter.regex.RegexTokenFilter;
import org.snipsnap.app.Application;
import org.snipsnap.serialization.StringBufferWriter;
import org.snipsnap.util.log.Logger;

import java.util.*;
import java.io.Writer;
import java.lang.reflect.Method;

/*
 * Class that finds snippets like
 * {link|neotis|http://www.neotis.de} ---> <elink ....>
 * {neotis} -> include neotis wiki
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
public class MacroFilter extends RegexTokenFilter {

  private static MacroFilter instance;

  private Map macros;
  private static Object monitor = new Object();
  private static Object[] noArguments =  new Object[]{};

  public MacroFilter() {
    super("\\{([^:}]+):?(.*?)\\}(.*?)\\{(\\1)\\}", SINGLELINE);
    addRegex("\\{([^:}]+):?(.*?)\\}", "", MULTILINE);

    macros = new HashMap();
    add("org.snipsnap.snip.filter.macro.FieldMacro");
    add("org.snipsnap.snip.filter.macro.LinkMacro");
    add("org.snipsnap.snip.filter.macro.AnnotationMacro");
    add("org.snipsnap.snip.filter.macro.CodeMacro");
    add("org.snipsnap.snip.filter.macro.ScriptMacro");
    add("org.snipsnap.snip.filter.macro.IsbnMacro");
    add("org.snipsnap.snip.filter.macro.ApiMacro");
    add("org.snipsnap.snip.filter.macro.TableMacro");
    add("org.snipsnap.snip.filter.macro.UserSnipMacro");
    add("org.snipsnap.snip.filter.macro.RecentWeblogMacro");
    add("org.snipsnap.snip.filter.macro.UserMacro");
    add("org.snipsnap.snip.filter.macro.SearchMacro");
    add("org.snipsnap.snip.filter.macro.IndexSnipMacro");
    add("org.snipsnap.snip.filter.macro.ImageMacro");
    add("org.snipsnap.snip.filter.macro.LastLoginMacro");
    add("org.snipsnap.snip.filter.macro.SinceLastVisitMacro");
    add("org.snipsnap.snip.filter.macro.LastVisitMacro");
    add("org.snipsnap.snip.filter.macro.HotSnipMacro");
    add("org.snipsnap.snip.filter.macro.QuoteMacro");
    add("org.snipsnap.snip.filter.macro.VersionMacro");
    add("org.snipsnap.snip.filter.macro.LoginsMacro");
    add("org.snipsnap.snip.filter.macro.RecentChangesMacro");
    add("org.snipsnap.snip.filter.macro.CalendarMacro");
    add("org.snipsnap.snip.filter.macro.MacroListMacro");
    add("org.snipsnap.snip.filter.macro.InterWikiMacro");
    add("org.snipsnap.snip.filter.macro.BackLinkMacro");
    add("org.snipsnap.snip.filter.macro.SnipLinkMacro");
    add("org.snipsnap.snip.filter.macro.LdapMacro");
    add("org.snipsnap.snip.filter.macro.ApiDocMacro");
  }

  public static MacroFilter getInstance() {
    synchronized (monitor) {
      if (null == instance) {
        instance = new MacroFilter();
      }
    }
    return instance;
  }

  public void add(String name) {
    try {
      Class macroClass = Class.forName(name);
      Macro macro;
      try {
        Method getInstanceMethod = null;
        getInstanceMethod = macroClass.getMethod("getInstance",null);
        macro = (Macro) getInstanceMethod.invoke(null, noArguments);
      } catch (NoSuchMethodException e) {
        macro = (Macro) macroClass.newInstance();
      }
      // System.err.println("MacroFilter: added "+name);
      add(macro);
      System.err.println("Added "+name);
    } catch (Exception e) {
      System.err.println("MacroFilter: unable to load '" + name + "' macro "+e);
    }
  }

  public void add(Macro macro) {
    macros.put(macro.getName(), macro);
  }

  public List getMacroList() {
    //@DANGER
    return new ArrayList(macros.values());
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, Snip snip) {
    String command = result.group(1);

    Logger.log("Parameter block:" + Application.get().getParameters() );

// {$peng} are variables not macros.
    if (!command.startsWith("$")) {
//      for (int i=0; i<result.groups(); i++) {
//        Logger.log("param("+i+") "+result.group(i));
//      }

      MacroParameter mParams = new MacroParameter();
      mParams.setSnip(snip);
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
            mParams.setContent(filter(mParams.getContent(), snip));
          }
          Writer writer = new StringBufferWriter(buffer);
          macro.execute(writer, mParams);
        } else if (command.startsWith("!")) {
// @TODO including of other snips
          Snip includeSnip = SnipSpace.getInstance().load(command.substring(1));
          if (null != includeSnip) {
            String included = includeSnip.getContent();
            // Filter paramFilter = new ParamFilter(mParams);
            // included = paramFilter.filter(included, null);
            buffer.append(included);
          } else {
            buffer.append(command.substring(1) + " not found.");
          }
          return;
        } else {
          buffer.append(result.group(0));
          return;
        }
      } catch (Exception e) {
        System.err.println("unable to format macro: " + result.group(1));
        e.printStackTrace();
        buffer.append("<div class=\"error\">?" + command + (result.length() > 1 ? ":" + result.group(2) : "") + "?</div>");
        return;

      }
    } else {
      buffer.append("<");
      buffer.append(command.substring(1));
      buffer.append(">");
    }
  }
}
