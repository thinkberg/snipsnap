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
