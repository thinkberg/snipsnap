package com.neotis.snip.filter;

/*
 * XmlCodeFilter colourizes Xml Code
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class XmlCodeFilter extends RegexReplaceFilter {

  private static final String KEYWORDS = "\\b(xsl:[^&]*)\\b";
  private static final String TAGS = "(&lt;.*?&gt;)";
  private static final String QUOTE = "\"(([^\"\\\\]|\\.)*)\"";

  public XmlCodeFilter() {
    super(QUOTE, "<style type=\"xml-quote\">\"$1\"</style>");
    addRegex(TAGS, "<style type=\"xml-tag\">$1</style>");
    addRegex(KEYWORDS, "<style type=\"xml-keyword\">$1</style>");
  };
}