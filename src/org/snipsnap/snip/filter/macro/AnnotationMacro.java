/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import com.neotis.snip.Snip;

public class AnnotationMacro extends Macro {
  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    if (params.length == 1) {
      return "<footnote>" + params[0] + "</footnote>";
    } else {
      throw new IllegalArgumentException("footnote needs exactly one argument");
    }
  }
}
