/*
 * Class that finds Regex and handles each token.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.regex;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Util;
import com.neotis.snip.Snip;

public abstract class RegexTokenFilter extends RegexFilter implements ActionMatch {

  /**
   * create a new regular expression and set
   */
  public RegexTokenFilter(String regex, boolean multiline) {
    addRegex(regex, "", multiline);
  }

  /**
   * create a new regular expression and set
   */
  public RegexTokenFilter(String regex) {
    addRegex(regex, "");
  }

  public abstract String handleMatch(MatchResult result, Snip snip);

  public String filter(String input, Snip snip) {
    String result = input;
    int size = pattern.size();
    for (int i = 0; i < size; i++) {
      Pattern p = (Pattern) pattern.get(i);
      String s = (String) substitute.get(i);
      result = Util.substitute(matcher, p, new ActionSubstitution(s, this, snip), result, limit);
    }
    return result;

  }
}