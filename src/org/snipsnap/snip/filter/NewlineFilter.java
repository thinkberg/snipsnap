/*
 * NewlineFilter finds \\ in its input and transforms this
 * to <newline/>
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class NewlineFilter extends RegexReplaceFilter {

  public NewlineFilter() {
    // matches \\
    super("\\\\\\\\", "<br/>");
  };
}
