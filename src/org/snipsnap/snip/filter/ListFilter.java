/*
 * NewlineFilter finds # in its input and transforms this
 * to <newline/>
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class ListFilter extends RegexReplaceFilter {

  public ListFilter() {
    super("^[:space:]*([-*])[:space:]?(?![-*])(.*)$", "<item type=\"$1\">$2</item>");
    addRegex("^[:space:]*([iIaA])\\.[:space:]?(?![iIaA]\\.)(.*)$", "<item type=\"$1\">$2</item>");
    addRegex("^[:space:]*\\d+\\.[:space:]?(?!\\d+\\.)(.*)$", "<item type=\"enumerated\">$1</item>");
    addRegex("((<item[^>]*>.*?</item>[\r]?[\n]?)+)", "<list>$1</list>\n", RegexReplaceFilter.SINGLELINE);
  };
}
