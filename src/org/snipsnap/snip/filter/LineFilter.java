/*
 * LineFilter finds ---- in its input and transforms this
 * to <line/>
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class LineFilter extends RegexReplaceFilter {

  public LineFilter() {
    super("-----*", "<line/>");
  };
}