/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */
/*
 * Macro that displays all Snips by user
 *
 * @author stephan
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipLink;
import com.neotis.snip.SnipSpace;
import org.apache.lucene.search.Hits;

import java.io.IOException;

public class SearchMacro extends Macro {
  SnipSpace space;

  public SearchMacro() {
    space = SnipSpace.getInstance();
  }

  public String getName() {
    return "search";
  }

  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    if (params.length == 1) {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<b>snips with ");
      buffer.append(params[0]);
      buffer.append(": (");
      Hits hits = space.search(params[0]);
      buffer.append(hits.length());

      buffer.append(") </b><br/>");

      if (hits.length() > 0) {
        int start = 0;
        final int MAX_HITS = 10;
        int end = Math.min(MAX_HITS, hits.length());
        buffer.append("<blockquote>");
        try {
          for (int i = start; i < end; i++) {
            SnipLink.appendLink(buffer, hits.doc(i).get("title"));
            if (i < end - 1) {
              buffer.append(", ");
            }
          }
          buffer.append("</blockquote>");
        } catch (IOException e) {
          System.err.println("I/O error while iterating over search results.");
        }
      } else {
        buffer.append("none found.");
      }
      return buffer.toString();
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
