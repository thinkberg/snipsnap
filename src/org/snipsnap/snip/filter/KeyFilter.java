/*
 * LinkFilter finds [text] in its input and transforms this
 * to <link name="text">
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class KeyFilter extends RegexReplaceFilter {

  public KeyFilter() {
    super("((Ctrl|Alt|Shift)-[^ ]*)", "<style type=\"key\">$1</style>");
  };
}