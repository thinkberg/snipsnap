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
/*
 * Created by IntelliJ IDEA.
 * User: leo
 * Date: May 13, 2002
 * Time: 1:50:23 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.neotis.snip.filter.macro;

import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;

public abstract class Preserved extends Macro {
  private Map special = new HashMap();
  private String specialString = "";

  protected void addSpecial(String c, String replacement) {
    specialString += c;
    special.put(c, replacement);
  }

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
