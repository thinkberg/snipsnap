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

package org.snipsnap.snip.filter.macro;

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.UserManager;
import org.snipsnap.app.Application;
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

  public void execute(StringBuffer buffer, String[] params, String content, Snip snip) throws IllegalArgumentException {
    if (params.length == 1 || params.length == 2) {
      int maxHits = 10;
      if (params.length == 2) {
          maxHits = Integer.parseInt(params[1]);
      }
      String searchString = params[0];

      buffer.append("<b>snips with ");
      buffer.append(searchString);
      buffer.append(": (");
      Hits hits = space.search(searchString);
      buffer.append(hits.length());

      buffer.append(") </b><br/>");

      if (hits.length() > 0) {
        int start = 0;
        int end = Math.min(maxHits, hits.length());
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

      if (! SnipSpace.getInstance().exists(searchString) &&
        UserManager.getInstance().isAuthenticated(Application.get().getUser())) {
          buffer.append("<p>There is no snip with <b>");
          buffer.append(searchString);
          buffer.append("</b> , would you like to ");
          SnipLink.createCreateLink(buffer, searchString);
          buffer.append(" ?</p>");
      }

      return;
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
