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
      return "<a href=\"" + params[1] + "\">" + params[0] + "</a>";
    } else if(params.length == 1) {
      return "<a href=\"" + params[0] + "\">" + params[0] + "</a>";
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
