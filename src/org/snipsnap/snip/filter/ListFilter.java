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
    super("^[:space:]*([-*])[:space:]?(?![-*])(.*)$", "<li type=\"$1\">$2</li>");
    addRegex("^[:space:]*([iIaA])\\.[:space:]?(?![iIaA]\\.)(.*)$", "<li type=\"$1\">$2</li>");
    addRegex("^[:space:]*\\d+\\.[:space:]?(?!\\d+\\.)(.*)$", "<li type=\"enumerated\">$1</li>");
    addRegex("((<li[^>]*>.*?</li>[\r]?[\n]?)+)", "<ul>$1</ul>\n", RegexReplaceFilter.SINGLELINE);
  };
}
