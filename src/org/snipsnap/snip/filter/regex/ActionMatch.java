/*
 * Interface for Classes that handle matches
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.regex;

import org.apache.oro.text.regex.MatchResult;
import com.neotis.snip.Snip;

public interface ActionMatch {
  public String handleMatch(MatchResult result, Snip snip);
}
