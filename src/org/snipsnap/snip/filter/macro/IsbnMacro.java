/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

public class IsbnMacro extends Macro {
  StringBuffer buffer;

  public IsbnMacro() {
    buffer = new StringBuffer();
  }
  public String execute(String[] params, String content) throws IllegalArgumentException {
    if (params.length== 1) {
      buffer.setLength(0);
      buffer.append("<img border=\"0\" alt=\">>\" src=\"/images/arrow.right.gif\"> (<a href=\"http://www.amazon.de/exec/obidos/ASIN/");
      buffer.append(params[0]);
      buffer.append("\">Amazon</a><a href=\"http://www.preistester.de/cgi-bin/pt/buchs.pl?query=profi&isbn=");
      buffer.append(params[0]);
      buffer.append("\">Preistester</a>)");
      return buffer.toString();
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
