/*
 * UrlFilter finds [text] in its input and transforms this
 * to <url name="text">
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class UrlFilter extends RegexReplaceFilter {

  public UrlFilter() {
    super("([^\"]|^)((http|ftp)s?://(%[[:digit:]A-Fa-f][[:digit:]A-Fa-f]|[-_.!~*';/?:@&=+$,[:alnum:]])+)",
             "$1<a href=\"$2\">$2</a>");
  };
}