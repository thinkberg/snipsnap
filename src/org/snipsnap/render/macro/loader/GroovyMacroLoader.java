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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Plugin loader for macros with Groovy source
 * instead of Java
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class GroovyMacroLoader extends MacroLoader {
  public Repository loadPlugins(Repository repository, Class klass) {
    if (null != repository) {
      try {
        String macroSource = "import java.io.Writer\n" +
        "import org.radeox.macro.parameter.MacroParameter\n" +
        "\n" +
        "class GroovyMacro extends org.radeox.macro.BaseMacro {\n" +
        "  void execute(Writer writer, MacroParameter params) {\n" +
        "    writer.write(\"Yipee ay ey, schweinebacke\")\n" +
        "  }\n" +
        "  String getName() {" +
        "    return \"groovy\"\n" +
        "  }\n " +
        "}";


        GroovyClassLoader gcl = new GroovyClassLoader();
        InputStream is = new ByteArrayInputStream(macroSource.getBytes());
        Class clazz = gcl.parseClass(is, "");

        //System.out.println(clazz.getName());
        // System.out.println(aScriptSource);

        Object aScript = clazz.newInstance();

        Macro macro = (Macro) aScript;
        add(repository, macro);
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Cannot load groovy macros: " + e.getMessage());
        //
      }
    }
    return repository;
  }
}