/*
 * Class that finds snippets like
 * {link|neotis|http://www.neotis.de} ---> <elink ....>
 * {neotis} -> include neotis wiki
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import com.neotis.snip.filter.regex.RegexTokenFilter;
import org.apache.oro.text.regex.MatchResult;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MacroFilter extends RegexTokenFilter {

  private Map macros = new HashMap();

  public MacroFilter() {
    super("\\{([^:}]*):?(.*?)\\}(.*?)\\{(\\1)\\}", SINGLELINE);
    addRegex("\\{([^:}]*):?(.*?)\\}", "", MULTILINE);

    macros.put("link", new LinkMacro());
    macros.put("note", new AnnotationMacro());
    macros.put("anno", new AnnotationMacro());
    macros.put("code", new CodeMacro());
    macros.put("isbn", new IsbnMacro());
    macros.put("api", new ApiMacro());
    macros.put("table", new TableMacro());
  }


  /**
   * Splits a String on a delimiter to a List. The function works like
   * the perl-function split.
   *
   * @param aString    a String to split
   * @param delimiter  a delimiter dividing the entries
   * @return           a Array of splittet Strings
   */

  public static String[] split(String aString, String delimiter) {
    StringTokenizer st = new StringTokenizer(aString, delimiter);
    String[] result = new String[st.countTokens()];
    int i = 0;

    while (st.hasMoreTokens()) {
      result[i++] = st.nextToken();
    }

    return result;
  }

  public String handleMatch(MatchResult result) {
    String[] params = null;
    String content = null;
    String command = result.group(1);


//    for (int i=0; i<result.groups(); i++) {
//      System.err.println(i+" "+result.group(i));
//    }

    // {tag} ... {tag}
    if (result.group(1).equals(result.group(result.groups() - 1))) {
      // {tag:1|2} ... {tag}
      if (!"".equals(result.group(2))) {
        params = split(result.group(2), "|");
      }
      content = result.group(3);
      // {tag}
    } else {
      if (result.groups() > 1) {
        params = split(result.group(2), "|");
      }
    }

    // @DANGER: recursive calls may replace macros in included source code
    try {
      if (macros.containsKey(command)) {
        Macro macro = (Macro) macros.get(command);
        // recursively filter macros within macros
        if (null != content) {
          content = filter(content);
        }
        return macro.execute(params, content);
      } else if (command.startsWith("!")) {
        // TODO including of other snips
        return "";
      } else {
        return result.group(0);
      }
    } catch (Exception e) {
      System.err.println("unable to format macro: " + result.group(1));
      e.printStackTrace();
      return "?" + result.group(1) + (result.length() > 1 ? ":" + result.group(2) : "") + "?";
    }
  }
}
