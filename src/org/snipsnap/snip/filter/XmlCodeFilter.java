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
    super(QUOTE, "<span class=\"xml-quote\">\"$1\"</class>");
    addRegex(TAGS, "<span class=\"xml-tag\">$1</class>");
    addRegex(KEYWORDS, "<span class=\"xml-keyword\">$1</class>");
  };
}