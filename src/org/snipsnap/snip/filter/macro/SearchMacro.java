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
import java.io.Writer;

public class SearchMacro extends Macro {
  SnipSpace space;

  public SearchMacro() {
    space = SnipSpace.getInstance();
  }

  public String getName() {
    return "search";
  }

  public void execute(Writer writer, String[] params, String content, Snip snip)
      throws IllegalArgumentException, IOException {

    if (params.length == 1 || params.length == 2) {
      int maxHits = 10;
      if (params.length == 2) {
          maxHits = Integer.parseInt(params[1]);
      }
      String searchString = params[0];


      Hits hits = null;
      try {
        hits = space.search(searchString);
      } catch (Exception e) {
        System.err.println("SearchMacro: exception while searching: "+e);
      }


      if (hits != null && hits.length() > 0) {
        writer.write("<div id=\"list\"><div class=\"list-title\">snips with ");
        writer.write(searchString);
        writer.write(": (");
        writer.write(""+hits.length());
        writer.write(")</div>");

        int start = 0;
        int end = Math.min(maxHits, hits.length());
        writer.write("<blockquote>");
        try {
          for (int i = start; i < end; i++) {
            SnipLink.appendLink(writer, hits.doc(i).get("title"));
            if (i < end - 1) {
              writer.write(", ");
            }
          }
          writer.write("</blockquote>");
        } catch (IOException e) {
          System.err.println("I/O error while iterating over search results.");
        }
      } else {
        writer.write("Nothing found.");
      }

      if (searchString != null && searchString.length() > 0 &&
        !SnipSpace.getInstance().exists(searchString) &&
        UserManager.getInstance().isAuthenticated(Application.get().getUser())) {
          writer.write("<p>There is no snip with <b>");
          writer.write(searchString);
          writer.write("</b> , would you like to ");
          SnipLink.createCreateLink(writer, searchString);
          writer.write("?</p>");
      }

      return;
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
