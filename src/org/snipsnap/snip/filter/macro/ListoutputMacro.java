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
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;

import java.util.Collection;
import java.util.Iterator;

/**
 * Base class for macros outputting a list, e.g. user-list
 *
 * @author stephan
 * @version $Id$
 */

public abstract class ListoutputMacro extends Macro {
  SnipSpace space;

  public ListoutputMacro() {
    space = SnipSpace.getInstance();
  }

  public void output(StringBuffer buffer, String explaination, Collection c, String emptyText) {
    buffer.append("<b>");
    buffer.append(explaination);
    buffer.append(": (");
    buffer.append(c.size());
    buffer.append(") </b><br/>");
    if (c.size() > 0) {
      buffer.append("<blockquote>");
      Iterator snipsIterator = c.iterator();
      while (snipsIterator.hasNext()) {
        Snip aSnip = (Snip) snipsIterator.next();
        SnipLink.appendLink(buffer, aSnip.getName());
        if (snipsIterator.hasNext()) {
          buffer.append(", ");
        }
      }
      buffer.append("</blockquote>");
    } else {
      buffer.append(emptyText);
    }
    return;
  }

  public abstract void execute(StringBuffer buffer, String[] params, String content, Snip snip) throws IllegalArgumentException;
}
