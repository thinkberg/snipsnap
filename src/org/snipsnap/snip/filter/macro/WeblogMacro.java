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

public class WeblogMacro extends Macro {
  StringBuffer buffer;
  SnipSpace space;

  public WeblogMacro() {
    buffer = new StringBuffer();
    space = SnipSpace.getInstance();
  }

  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    if (params.length == 0) {
      buffer.setLength(0);
      List snips = snip.getChildren();
      Iterator iterator = snips.iterator();
      while (iterator.hasNext()) {
        Snip entry = (Snip) iterator.next();
        buffer.append("<b>");
        buffer.append(entry.getName());
        buffer.append("</b><p>");
        buffer.append(entry.toXML());
        buffer.append("</p>");
        buffer.append(snip.getComments().getCommentString());
        buffer.append("<hr/>");
      }

      return buffer.toString();
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
