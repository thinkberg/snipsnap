/*
 * JavaCodeFilter colourizes Java Code
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class SqlCodeFilter extends RegexReplaceFilter {

  private static final String KEYWORDS =
      "\\b(SELECT|DELETE|UPDATE|WHERE|FROM|GROUP|BY|HAVING)\\b";

  private static final String OBJECTS =
      "\\b(VARCHAR)" +
      "\\b";

  private static final String QUOTES =
      "\"(([^\"\\\\]|\\.)*)\"";


  public SqlCodeFilter() {
    super(QUOTES, "<span class=\"sql-quote\">\"$1\"</class>");
    addRegex(OBJECTS, "<span class=\"sql-object\">$1</class>");
    addRegex(KEYWORDS, "<span class=\"sql-keyword\">$1</class>");
  };
}