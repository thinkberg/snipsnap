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

package org.snipsnap.render.macro.loader;

import groovy.lang.GroovyClassLoader;
import org.radeox.macro.Macro;
import org.radeox.macro.MacroLoader;
import org.radeox.macro.Repository;
import org.snipsnap.container.Components;
import org.snipsnap.notification.Consumer;
import org.snipsnap.notification.Message;
import org.snipsnap.notification.MessageService;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpace;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Plugin loader for macros with Groovy source
 * instead of Java
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class GroovyMacroLoader extends MacroLoader implements Consumer {
  public GroovyMacroLoader() {
    // We're interested in changed snips
    MessageService service = (MessageService) Components.getComponent(MessageService.class);
    service.register(this);
  }

  public void consume(Message messsage) {
    if (Message.SNIP_MODIFIED.equals(messsage.getType())) {
      Snip snip = (Snip) messsage.getValue();
      if (snip.getName().startsWith("SnipSnap/config/macros/")) {
        try {
          Macro macro = compileMacro(snip.getContent());
          if (null != macro) {
            add(repository, macro);
          }
        } catch (Exception e) {
          System.err.println("GroovyMacroLoader: unable to reload macros: " + e);
          e.printStackTrace();
        }
      }
    }
  }

  private Macro compileMacro(String macroSource) {
    Macro macro = null;
    try {
      GroovyClassLoader gcl = new GroovyClassLoader();
      InputStream is = new ByteArrayInputStream(macroSource.getBytes());
      Class clazz = gcl.parseClass(is, "");
      Object aScript = clazz.newInstance();
      macro = (Macro) aScript;
      //System.out.println("Script="+macroSource);
    } catch (Exception e) {
      System.err.println("Cannot compile groovy macro: " + e.getMessage());
      e.printStackTrace();
    }
    return macro;
  }

  /**
   * Load all plugins of Class klass
   *
   * @param repository
   * @param klass
   * @return
   */
  public Repository loadPlugins(Repository repository, Class klass) {
    if (null != repository) {
      snipsnap.api.snip.SnipSpace space = (snipsnap.api.snip.SnipSpace) Components.getComponent(snipsnap.api.snip.SnipSpace.class);
      snipsnap.api.snip.Snip[] snips = space.match("SnipSnap/config/macros/");

      for (int i = 0; i < snips.length; i++) {
        Snip snip = snips[i];
        Macro macro = compileMacro(snip.getContent());
        add(repository, macro);
      }

//      String macroSource = "import java.io.Writer\n" +
//          "import org.radeox.macro.parameter.MacroParameter\n" +
//          "\n" +
//          "class GroovyMacro extends org.radeox.macro.BaseMacro {\n" +
//          "  void execute(Writer writer, MacroParameter params) {\n" +
//          "    writer.write(\"Yipee ay ey, schweinebacke\")\n" +
//          "  }\n" +
//          "  String getName() {" +
//          "    return \"groovy\"\n" +
//          "  }\n " +
//          "}";
//      Macro macro = compileMacro(macroSource);
//      if (null != macro) {
//        add(repository, macro);
//      }

      //System.out.println(clazz.getName());
      // System.out.println(aScriptSource);
    }
    return repository;
  }
}