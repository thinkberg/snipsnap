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
    super(QUOTES, "<style type=\"sql-quote\">\"$1\"</style>");
    addRegex(OBJECTS, "<style type=\"sql-object\">$1</style>");
    addRegex(KEYWORDS, "<style type=\"sql-keyword\">$1</style>");
  };
}