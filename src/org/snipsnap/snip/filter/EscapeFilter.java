/*
 * Transforms multiple \ into single backspaces and escapes other characters.
 *
 * @author leo
 * @team other
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexTokenFilter;
import org.apache.oro.text.regex.MatchResult;

public class EscapeFilter extends RegexTokenFilter {

  public EscapeFilter() {
    super("\\\\(\\\\\\\\)|\\\\(.)");
  }

  public String handleMatch(MatchResult result) {
    if (result.group(1) == null) {
      String match = result.group(2);
      if("\\".equals(match)) {
        return "\\\\";
      }
      return "&#x" + Integer.toHexString((int) result.group(2).charAt(0)) + ";";
    } else {
      return "&#x005c;";
    }
  }

}
