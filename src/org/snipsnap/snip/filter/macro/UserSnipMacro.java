/*
 * Macro that displays all Snips by user
 *
 * @author stephan
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

import java.util.Iterator;
import java.util.List;

public class UserSnipMacro extends Macro {
  StringBuffer buffer;
  SnipSpace space;

  public UserSnipMacro() {
    buffer = new StringBuffer();
    space = SnipSpace.getInstance();
  }

  public String execute(String[] params, String content) throws IllegalArgumentException {
    if (params.length == 1) {
      buffer.setLength(0);
      buffer.append("<b>this user's snips: (");
      List snips = space.getByUser(params[0]);
      buffer.append(snips.size());
      buffer.append(") </b><br/>");
      if (snips.size()>0) {
      buffer.append("<blockquote>");
      Iterator snipsIterator = snips.iterator();
      while (snipsIterator.hasNext()) {
        Snip snip = (Snip) snipsIterator.next();
        buffer.append("<a href=\"/space/");
        buffer.append(snip.getName());
        buffer.append("\">");
        buffer.append(snip.getName());
        buffer.append("</a>");
        if (snipsIterator.hasNext()) {
          buffer.append(", ");
        }
      }
      buffer.append("</blockquote>");
      } else {
        buffer.append("none written yet.");
      }
      return buffer.toString();
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
