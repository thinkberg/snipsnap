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

package org.snipsnap.feeder;

import groovy.lang.GroovyClassLoader;
import org.radeox.macro.Repository;
import org.snipsnap.container.Components;
import org.snipsnap.notification.Consumer;
import org.snipsnap.notification.Message;
import org.snipsnap.notification.MessageService;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import org.snipsnap.user.Security;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Plugin loader for feeders with Groovy source
 * instead of Java
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class GroovyFeederLoader extends FeederLoader implements Consumer {
  public final static String SYSTEM_FEEDER_PATH = "SnipSnap/config/feeder/";
  private final static Roles EXEC_ROLES = new Roles(Roles.ADMIN);

  public GroovyFeederLoader() {
    // We're interested in changed snips
    MessageService service = (MessageService) Components.getComponent(MessageService.class);
    service.register(this);
  }

  public void consume(Message messsage) {
//    System.out.println("GroovyFeederLoader: Message received.");
    if (Message.SNIP_MODIFIED.equals(messsage.getType())) {
      Snip snip = (Snip) messsage.getValue();
      if (snip.getName().startsWith(SYSTEM_FEEDER_PATH) &&
        Security.existsPermission(Permissions.EDIT_SNIP, snip, EXEC_ROLES)) {
        try {
          Feeder feeder = compileFeeder(snip.getContent());
          if (null != feeder) {
            add(repository, feeder);
          }
        } catch (Exception e) {
          System.err.println("GroovyFeederLoader: unable to reload feeders: " + e);
          e.printStackTrace();
        }
      }
    }
  }

  private Feeder compileFeeder(String macroSource) {
    Feeder feeder = null;
    try {
      GroovyClassLoader gcl = new GroovyClassLoader();
      InputStream is = new ByteArrayInputStream(macroSource.getBytes());
      Class clazz = gcl.parseClass(is, "");
      Object aScript = clazz.newInstance();
      feeder = (Feeder) aScript;
      System.out.println("Compiled: " + feeder.getName());
      //System.out.println("Script="+macroSource);
    } catch (Exception e) {
      System.err.println("Cannot compile groovy feeder: " + e.getMessage());
      e.printStackTrace();
    }
    return feeder;
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
      SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
      Snip[] snips = space.match(SYSTEM_FEEDER_PATH);

      for (int i = 0; i < snips.length; i++) {
        Snip snip = snips[i];
        Feeder feeder = compileFeeder(snip.getContent());
        add(repository, feeder);
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