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

package org.snipsnap.snip.filter.macro;

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.filter.macro.list.SimpleList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.Writer;
import java.io.IOException;

/**
 * Base class for macros outputting a list, e.g. user-list
 *
 * @author stephan
 * @version $Id$
 */

public abstract class ListoutputMacro extends Macro {
  protected SnipSpace space = SnipSpace.getInstance();

  public interface ListFormatter {
    public void format(Writer writer, String listComment, Collection c, String emptyText) throws IOException ;
  }

  private final static String FORMATTER_PREFIX = "org.snipsnap.snip.filter.macro.list";
  private final static String FORMATTER_SUFFIX = "ListFormatter";
  private Map formatterMap = new HashMap();


  public void output(Writer writer, String listComment, Collection c, String emptyText, String style) throws IOException {
    ListFormatter formatter = (ListFormatter) formatterMap.get(style);
    if (formatter == null && style != null && style.length() > 0) {
      try {
        formatter = (ListFormatter) Class.forName(FORMATTER_PREFIX + "." + style + FORMATTER_SUFFIX).newInstance();
      } catch (Exception e) {
        try {
          formatter = (ListFormatter) Class.forName(style).newInstance();
        } catch (Exception e1) {
          System.err.println("ListoutputMacro: error loading list formatter: " + style + FORMATTER_SUFFIX);
          System.err.println("ListoutputMacro: neither '" + FORMATTER_PREFIX + "." + style + FORMATTER_SUFFIX + "' nor '"
                             + style + "' is loadable");
          e.printStackTrace();
          e1.printStackTrace();
        }
      }
      if (formatter != null) {
        formatterMap.put(style, formatter);
      }
    }

    if (formatter != null) {
      formatter.format(writer, listComment, c, emptyText);
    } else {
      output(writer, listComment, c, emptyText);
    }
  }

  private final ListFormatter defaultFormatter = new SimpleList();

  public void output(Writer writer, String listComment, Collection c, String emptyText) throws IOException {
    defaultFormatter.format(writer, listComment, c, emptyText);
  }

  public abstract void execute(Writer writer, String[] params, String content, Snip snip) throws IllegalArgumentException, IOException;
}

