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
    StringBuffer result = new StringBuffer("<table class=\"snip-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
    StringBuffer cell = new StringBuffer();
    StringBuffer row = new StringBuffer();
    boolean firstLine = true;
    boolean odd = true;
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if ("|".equals(token)) {
        cell.insert(0, "<td>").append("</td>");
        row.append(cell);
        cell = new StringBuffer();
      } else if ("\n".equals(token)) {
        // add rest of cell
        cell.insert(0, "<td>").append("</td>");
        row.append(cell);
        cell = new StringBuffer();

        // add row
        result.append("<tr valign=\"top\"");
        if (firstLine) {
          result.append(" class=\"snip-table-header\">");
          firstLine = false;
        } else if (odd) {
          result.append(" class=\"snip-table-odd\">");
          odd = false;
        } else {
          result.append(" class=\"snip-table-even\">");
          odd = true;
        }
        result.append(row).append("</tr>\n");
        row = new StringBuffer();
      } else {
        cell.append(token);
      }
    }
    result.append("</table>");
    return result.toString().trim();
  }

}
