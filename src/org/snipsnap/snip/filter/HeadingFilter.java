/*
 * Transforms header style lines into subsections.
 *
 * @author leo
 * @team other
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexTokenFilter;
import org.apache.oro.text.regex.MatchResult;

public class HeadingFilter extends RegexTokenFilter {

  public HeadingFilter() {
    super("^[:space:]*(1(\\.1)*) (.*?)[:space:]*$");
  }

  public String handleMatch(MatchResult result) {
    String indent = result.group(1).replace('.', '-');
    return "<div class=\"heading-"+indent+"\">" + result.group(3) + "</div>";
  }
}
