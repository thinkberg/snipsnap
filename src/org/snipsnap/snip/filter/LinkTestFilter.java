/*
 * LinkTestFilter finds [text] in its input and transforms this
 * to <link name="text">
 * Additonally it checks, if the link really exists.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;

import java.net.URLEncoder;

public class LinkTestFilter extends Filter {

  LinkTester linkTester;
  int limit = Util.SUBSTITUTE_ALL;
  int interps = Perl5Substitution.INTERPOLATE_ALL;
  PatternMatcher matcher = new Perl5Matcher();
  Pattern pattern = null;
  PatternCompiler compiler = new Perl5Compiler();
  String _substitute;

  public LinkTestFilter(LinkTester linkTester) {
    this.linkTester = linkTester;

    try {
      pattern = compiler.compile("\\[(.*?)\\]");
    } catch (MalformedPatternException e) {
      System.err.println("error compiling pattern: "+e);
    }

    // super("\\[(.*?)\\]", "<link href=\"$1\"/>");
  }

  public String filter(String input) {
    StringBuffer buffer = new StringBuffer("");

    PatternMatcherInput patternMatcherInput = new PatternMatcherInput(input);

    int lastmatch = 0;

    // Loop until there are no more matches left.
    MatchResult result;
    while (matcher.contains(patternMatcherInput, pattern)) {
      // Since we're still in the loop, fetch match that was found.
      result = matcher.getMatch();
      buffer.append(input.substring(lastmatch, result.beginOffset(0)));

      if(linkTester.exists(result.group(1))) {
        buffer.append("[<a href=\"");
        buffer.append("/space/");
        buffer.append(URLEncoder.encode(result.group(1)));
        buffer.append("\">").append(result.group(1)).append("</a>]");
      } else {
        buffer.append("<a href=\"");
        buffer.append("/exec/edit?name=");
        buffer.append(URLEncoder.encode(result.group(1)));
        buffer.append("\">create ").append(result.group(1)).append("</a>");
      }


/*
      try {
        buffer.append(URLEncoder.encode(result.group(1), "ISO-8859-1"));
      } catch (UnsupportedEncodingException e) {
        cat.error("unsupported encoding", e);
        buffer.append(result.group(1));
      }
*/
      lastmatch = result.endOffset(0);
    }
    buffer.append(input.substring(lastmatch));
    return buffer.toString();

  }
}