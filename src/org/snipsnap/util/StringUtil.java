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
package org.snipsnap.util;

/**
 * Helper utility for string handling.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class StringUtil {
  public static String plural(int i, String s1, String s2) {
    StringBuffer buffer = new StringBuffer();
    return plural(buffer, i, s1, s2).toString();
  }

  public static String plural(int i, String s) {
    StringBuffer buffer = new StringBuffer();
    return plural(buffer, i, s).toString();
  }

  public static StringBuffer plural(StringBuffer buffer, int i, String s1, String s2) {
    buffer.append(i);
    buffer.append(" ");
    if (i > 1 || i == 0) {
      buffer.append(s1);
    } else {
      buffer.append(s2);
    }
    return buffer;
  }

  public static StringBuffer plural(StringBuffer buffer, int i, String s) {
    buffer.append(i);
    buffer.append(" ");
    buffer.append(s);
    if (i > 1 || i == 0) {
      buffer.append("s");
    }
    return buffer;
  }
}
