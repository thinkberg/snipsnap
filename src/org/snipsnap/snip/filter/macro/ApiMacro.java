/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import com.neotis.snip.Snip;


public class ApiMacro extends Macro {
  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    String mode;

    if (params.length == 1) {
      mode = "java";
    } else if (params.length == 2) {
      mode = params[1].toLowerCase();
    } else {
      throw new IllegalArgumentException("api macro needs one or two paramaters");
    }

    StringBuffer url = new StringBuffer();

    if ("java".equals(mode)) {
      // Transform java.lang.StringBuffer to
      // http://java.sun.com/j2se/1.4/docs/api/java/lang/StringBuffer.html
      url.append("http://java.sun.com/j2se/1.4/docs/api/");
      url.append(params[0].replace('.', '/'));
      url.append(".html");

    } else if ("ruby".equals(mode)) {
      url.append("http://www.rubycentral.com/book/ref_c_");
      url.append(params[0].toLowerCase());
      url.append(".html");
    }
    return "<a href=\"" + params[0] + "\"<a href=\"" + url.toString() + "\">" + params[0] + "</a>";
  }
}
