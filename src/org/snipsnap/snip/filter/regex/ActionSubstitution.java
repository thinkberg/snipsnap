/*
 * Class to use with ORO Regex substitution.
 * ActionSubstitution triggers an action on every match
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.regex;

import org.apache.oro.text.regex.*;

public class ActionSubstitution extends StringSubstitution {
  ActionMatch actionMatch = null;

  public ActionSubstitution(String s, ActionMatch actionMatch) {
    super(s);
    this.actionMatch = actionMatch;
  }

  public void appendSubstitution(StringBuffer stringBuffer, MatchResult matchResult,
                                 int i, PatternMatcherInput patternMatcherInput,
                                 PatternMatcher patternMatcher, Pattern pattern) {
    setSubstitution(actionMatch.handleMatch(matchResult));
    super.appendSubstitution(stringBuffer, matchResult, i, patternMatcherInput,
        patternMatcher, pattern);
  }
}
