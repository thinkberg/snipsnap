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

import org.snipsnap.render.filter.EscapeFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A specialized macro that allows to preserve certain special characters
 * by creating character entities. The subclassing macro may decide whether
 * to call replace() before or after executing the actual macro substitution.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public abstract class Preserved extends Macro {
  private Map special = new HashMap();
  private String specialString = "";

  /**
   * Escape special character c by replacing with it's hex character entity code.
   */
  protected void addSpecial(char c) {
    addSpecial("" + c, EscapeFilter.escape(c));
  }

  /**
   * Add a replacement for the special character c which may be a string
   * @param c the character to replace
   * @param replacement the new string
   */
  protected void addSpecial(String c, String replacement) {
    specialString += c;
    special.put(c, replacement);
  }

  /**
   * Actually replace specials in source.
   */
  protected String replace(String source) {
    StringBuffer tmp = new StringBuffer();
    StringTokenizer stringTokenizer = new StringTokenizer(source, specialString, true);
    while (stringTokenizer.hasMoreTokens()) {
      String current = stringTokenizer.nextToken();
      if (special.containsKey(current)) {
        current = (String) special.get(current);
      }
      tmp.append(current);
    }
    return tmp.toString();
  }
}
