/*
 * JavaCodeFilter colourizes Java Code
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class JavaCodeFilter extends RegexReplaceFilter {

  private static final String KEYWORDS =
      "\\b(abstract|break|byvalue|case|cast|catch|" +
      "class|const|continue|default|do|else|extends|" +
      "false|final|finally|for|future|generic|goto|if|" +
      "implements|import|inner|instanceof|interface|" +
      "native|new|null|operator|outer|package|private|" +
      "protected|public|rest|return|static|super|switch|" +
      "synchronized|this|throw|throws|transient|true|try|" +
      "var|volatile|while)\\b";

  private static final String OBJECTS =
      "\\b(Boolean|Byte|Character|Class|ClassLoader|Cloneable|Compiler|" +
      "Double|Float|Integer|Long|Math|Number|Object|Process|" +
      "Runnable|Runtime|SecurityManager|Short|String|StringBuffer|" +
      "System|Thread|ThreadGroup|Void|boolean|char|byte|short|int|long|float|double)\\b";

  private static final String QUOTES =
      "\"(([^\"\\\\]|\\.)*)\"";


  public JavaCodeFilter() {
    super(QUOTES, "<span class=\"java-quote\">\"$1\"</span>");
    addRegex(OBJECTS, "<span class=\"java-object\">$1</span>");
    addRegex(KEYWORDS, "<span class=\"java-keyword\">$1</span>");
  };
}