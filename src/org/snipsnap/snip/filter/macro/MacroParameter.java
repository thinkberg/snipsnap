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
import org.snipsnap.app.Application;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.HashMap;

/**
 * Encapsulates parameters for an execute Macro call
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class MacroParameter {
  private Snip snip;
  private String content;
  private Map params;
  private int size;

  public MacroParameter() {
    params = new HashMap();
  }

  public MacroParameter(String stringParams) {
    setParams(stringParams);
  }

  public Snip getSnip() {
    return snip;
  }

  public void setSnip(Snip snip) {
    this.snip = snip;
  }

  public void setParams(String stringParams) {
    params = split(stringParams, "|");
    size = params.size();
   }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getLength() {
    return size;
  }

  public String get(String index) {
    return (String) params.get(index);
  }

  public String get(int index) {
    return get(""+index);

  }

  /**
   *
   * Splits a String on a delimiter to a List. The function works like
   * the perl-function split.
   *
   * @param aString    a String to split
   * @param delimiter  a delimiter dividing the entries
   * @return           a Array of splittet Strings
   */

  public Map split(String aString, String delimiter) {
    Map globals = Application.get().getParameters();
    StringTokenizer st = new StringTokenizer(aString, delimiter);
    Map result = new HashMap();
    int i = 0;

    while (st.hasMoreTokens()) {
      String value = st.nextToken();
      String key = ""+i;
      if (value.startsWith("$")) {
        value = value.substring(1);
        if (globals.containsKey(value)) {
          result.put(""+i, (String) globals.get(value));
        } else {
          result.put(""+i, "");
        }
      } else {
        result.put(""+i, value);
      }
      i++;
    }

    return result;
  }
}
