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

package org.snipsnap.render.macro;

import org.snipsnap.render.macro.list.ListFormatter;
import org.snipsnap.render.macro.list.SimpleList;
import org.snipsnap.render.macro.parameter.MacroParameter;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.util.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base class for macros outputting a list, e.g. user-list
 *
 * @author stephan
 * @version $Id$
 */

public abstract class ListOutputMacro extends SnipMacro {

  private static Map formatterMap = new HashMap();

  static {
    /* load all list formatter found in the services plugin control file */
    Iterator macroIt = Service.providers(ListFormatter.class);
    while (macroIt.hasNext()) {
      try {
        ListFormatter formatter = (ListFormatter) macroIt.next();
        formatterMap.put(formatter.getName().toLowerCase(), formatter);
        System.err.println("Loaded list formatter: " + formatter.getName());
      } catch (Exception e) {
        System.err.println("ListOutputMacro: unable to load list formatter: " + e);
        e.printStackTrace();
      }
    }

  }

  private final static ListFormatter defaultFormatter = new SimpleList();

  public void output(Writer writer, String listComment, Collection c, String emptyText, String style, boolean showSize) throws IOException {
    ListFormatter formatter = (ListFormatter) ListOutputMacro.formatterMap.get(style != null ? style.toLowerCase() : null);

    if (formatter != null) {
      formatter.format(writer, listComment, c, emptyText, showSize);
    } else {
      ListOutputMacro.defaultFormatter.format(writer, listComment, c, emptyText, showSize);
    }
  }


  public abstract void execute(Writer writer, SnipMacroParameter params) throws IllegalArgumentException, IOException;
}

