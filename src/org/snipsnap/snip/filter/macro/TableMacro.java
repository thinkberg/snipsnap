/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import java.util.StringTokenizer;

public class TableMacro extends Macro {

  public String execute(String[] params, String content) throws IllegalArgumentException {
    content = content.trim()+"\n";

    StringTokenizer tokenizer = new StringTokenizer(content, "|\n", true);
    StringBuffer result = new StringBuffer("<table class=\"wiki-table\">");
    StringBuffer cell = new StringBuffer();
    StringBuffer row = new StringBuffer();
    boolean firstLine = true;
    boolean odd = true;
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if ("|".equals(token)) {
        cell.insert(0, "<cell>").append("</cell>");
        row.append(cell);
        cell = new StringBuffer();
      } else if ("\n".equals(token)) {
        // add rest of cell
        cell.insert(0, "<cell>").append("</cell>");
        row.append(cell);
        cell = new StringBuffer();

        // add row
        result.append("<row");
        if (firstLine) {
          result.append(" style=\"wiki-table-header\">");
          firstLine = false;
        } else if (odd) {
          result.append(" style=\"wiki-table-odd\">");
          odd = false;
        } else {
          result.append(" style=\"wiki-table-even\">");
          odd = true;
        }
        result.append(row).append("</row>\n");
        row = new StringBuffer();
      } else {
        cell.append(token);
      }
    }
    result.append("</table>");
    return result.toString().trim();
  }

}
