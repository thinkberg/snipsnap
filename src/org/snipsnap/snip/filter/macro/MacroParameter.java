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
import org.snipsnap.util.log.Logger;

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
    Logger.log(params.toString());
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

  public String get(String index, int idx) {
    String result = get(index);
    if(result == null) {
      result = get(idx);
    }
    return result;
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
    StringTokenizer st = new StringTokenizer(aString, delimiter);
    Map result = new HashMap();
    int i = 0;

    while (st.hasMoreTokens()) {
      String value = st.nextToken();
      String key = ""+i;
      if (value.indexOf("=") != -1) {
        // Store this for
        result.put(key, insertValue(value));
        int index = value.indexOf("=");
        key = value.substring(0, index);
        value = value.substring(index+1);

        result.put(key, insertValue(value));
      } else {
        result.put(key, insertValue(value));
      }
      i++;
    }

    return result;
  }

  private String insertValue(String s) {
    int idx = s.indexOf('$');
    StringBuffer tmp = new StringBuffer();
    if(idx != -1) {
      Map globals = Application.get().getParameters();
      String var = s.substring(idx+1);
      if(idx > 0) tmp.append(s.substring(0, idx));
      if(globals.containsKey(var)) {
        tmp.append(globals.get(var));
      }
      return tmp.toString();
    }
    return s;
  }
}
