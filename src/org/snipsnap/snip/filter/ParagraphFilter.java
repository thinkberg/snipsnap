/*
 * The paragraph filter finds any text between two empty lines and sourrounds
 * that text with a <p> ... </p> tag.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class ParagraphFilter extends RegexReplaceFilter {

  public ParagraphFilter() {
    // match anything between two empty lines (normalize sequential empty lines to one)
    super("([ \t\r]*[\n]){2,}", "<p></p>$1", RegexReplaceFilter.SINGLELINE);
  };
}
