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

public class ItalicFilter extends RegexReplaceFilter {

  public ItalicFilter() {
    super("~~(.*?)~~", "<italic>$1</italic>");
  };
}