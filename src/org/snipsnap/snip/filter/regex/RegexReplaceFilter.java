/*
 * Class that applies a RegexFilter, can be subclassed
 * for special Filters
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter.regex;

import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;

public class RegexReplaceFilter extends RegexFilter {

  /**
   * create a new regular expression that takes input as multiple lines
   */
  public RegexReplaceFilter(String regex, String substitute) {
    addRegex(regex, substitute);
  }

  /**
   * create a new regular expression and set
   */
  public RegexReplaceFilter(String regex, String substitute, boolean multiline) {
    addRegex(regex, substitute, multiline);
  }

  public String filter(String input) {
    String result = input;
    int size = pattern.size();
    for (int i = 0; i < size; i++) {
      Pattern p = (Pattern) pattern.get(i);
      String s = (String) substitute.get(i);
      result = Util.substitute(matcher, p, new Perl5Substitution(s, interps), result, limit);
    }
    return result;

  }
}