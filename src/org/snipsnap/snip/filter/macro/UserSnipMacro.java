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

public class UserSnipMacro extends Macro {
  StringBuffer buffer;
	SnipSpace space;

  public UserSnipMacro() {
    buffer = new StringBuffer();
		space = SnipSpace.getInstance();
  }

  public String execute(String[] params, String content) throws IllegalArgumentException {
    if (params.length== 1) {
      buffer.setLength(0);
			buffer.append("<b>this user's snips:</b><br/><blockquote>");
			Iterator snips = space.getByUser(params[0]).iterator();
			while (snips.hasNext()) {
				 Snip snip = (Snip) snips.next();
				 buffer.append("<a href=\"/space/");
				 buffer.append(snip.getName());
				 buffer.append("\">");
				 buffer.append(snip.getName());
				 buffer.append("</a>");
				 if (snips.hasNext()) {
					 buffer.append(", ");
				 }
			}
			buffer.append("</blockquote>");
      return buffer.toString();
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
