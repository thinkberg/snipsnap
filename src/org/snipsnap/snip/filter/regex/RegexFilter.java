
/*
 * Class that applies a RegexFilter, can be subclassed
 * for special Filters
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.regex;

import org.apache.oro.text.regex.*;

import java.util.List;
import java.util.ArrayList;

import com.neotis.snip.filter.Filter;
import com.neotis.snip.Snip;

public abstract class RegexFilter extends Filter {
  int limit = Util.SUBSTITUTE_ALL;
  int interps = Perl5Substitution.INTERPOLATE_ALL;
  PatternMatcher matcher = new Perl5Matcher();
  PatternCompiler compiler = new Perl5Compiler();
  List pattern = new ArrayList();
  List substitute = new ArrayList();
  public final static boolean MULTILINE = true;
  public final static boolean SINGLELINE = false;

  public void addRegex(String regex, String substitute) {
    addRegex(regex, substitute, MULTILINE);
  }

  public void addRegex(String regex, String substitute, boolean multiline) {
    try {
      this.pattern.add(compiler.compile(regex, multiline ? Perl5Compiler.MULTILINE_MASK : Perl5Compiler.SINGLELINE_MASK));
      this.substitute.add(substitute);
    } catch(MalformedPatternException e) {
      System.err.println("bad pattern: " + e);
    }
  }

  public abstract String filter(String input, Snip snip);
}
