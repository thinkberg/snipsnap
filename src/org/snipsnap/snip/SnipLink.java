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

package com.neotis.snip;

/**
 *  Generates links for snips
 *
 * @author stephan
 * @version $Id$
 */

public class SnipLink {
  public static String createLink(String name, String view) {
    StringBuffer buffer = new StringBuffer();
    return appendLink(buffer, name, view).toString();
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name) {
    return appendLink(buffer, name, name);
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name, String view) {
    buffer.append("<a href=\"/space/");
    buffer.append(name);
    buffer.append("\">");
    buffer.append(view);
    buffer.append("</a>");
    return buffer;
  }
}
