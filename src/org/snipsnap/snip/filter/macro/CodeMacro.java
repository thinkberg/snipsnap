/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import com.neotis.snip.filter.Filter;
import com.neotis.snip.filter.JavaCodeFilter;
import com.neotis.snip.filter.SqlCodeFilter;
import com.neotis.snip.filter.XmlCodeFilter;

import java.util.HashMap;
import java.util.Map;

public class CodeMacro extends Preserved {
  private Map filters;

  public CodeMacro() {
    filters = new HashMap();
    filters.put("xml", new XmlCodeFilter());
    filters.put("java", new JavaCodeFilter());
    filters.put("sql", new SqlCodeFilter());

    addSpecial("[", "&#x005b;");
    addSpecial("]", "&#x005d;");
    addSpecial("{", "&#x007b;");
    addSpecial("}", "&#x007d;");
  }

  public String execute(String[] params, String content) throws IllegalArgumentException {
    Filter filter = null;

    if (params == null || !filters.containsKey(params[0])) {
      filter = (Filter) filters.get("java");
    } else {
      filter = (Filter) filters.get(params[0]);
    }
    String result = filter.filter(content);

    return "<div class=\"code\">" + replace(result.trim()) + "</div>";
  }
}
