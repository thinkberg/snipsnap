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
import com.neotis.snip.Snip;

public class ActionSubstitution extends StringSubstitution {
  ActionMatch actionMatch = null;
  Snip snip = null;

  public ActionSubstitution(String s, ActionMatch actionMatch, Snip snip) {
    super(s);
    this.actionMatch = actionMatch;
    this.snip = snip;
  }

  public void appendSubstitution(StringBuffer stringBuffer, MatchResult matchResult,
                                 int i, PatternMatcherInput patternMatcherInput,
                                 PatternMatcher patternMatcher, Pattern pattern) {
    setSubstitution(actionMatch.handleMatch(matchResult, snip));
    super.appendSubstitution(stringBuffer, matchResult, i, patternMatcherInput,
        patternMatcher, pattern);
  }
}
