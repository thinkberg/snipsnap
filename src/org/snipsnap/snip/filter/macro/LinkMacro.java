/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

public class LinkMacro extends Macro {
  public String execute(String[] params, String content) throws IllegalArgumentException {
    if (params.length == 2) {
      return "<elink href=\"" + params[1] + "\" name=\"" + params[0] + "\"/>";
    } else if(params.length == 1) {
      return "<elink href=\"" + params[0] + "\" name=\"" + params[0] + "\"/>";
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
