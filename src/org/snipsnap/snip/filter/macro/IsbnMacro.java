/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

public class IsbnMacro extends Macro {
  public String execute(String[] params, String content) throws IllegalArgumentException {
    if (params.length == 1) {
      return "<book isbn=\"" + params[0] + "\"/>";
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
