/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2004 Stephan J. Schmidt, Matthias L. Jugel
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


package org.snipsnap.render.macro;

import org.apache.lucene.search.Hits;
import org.radeox.macro.BaseMacro;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.AuthenticationService;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro for xref searches in SnipSnap. The macro
 * displays the snips reference to the snip for the input string.
 *
 * @author Mario Ivankovits
 * @version $Id$
 */

public class SnipXrefMacro extends BaseMacro {
  private SnipSpace space;

  private String[] paramDescription =
    {"1: snip that is referenced",
     "?2: number of hits to show, defaults to 10"};

  public SnipXrefMacro() {
    space = SnipSpaceFactory.getInstance();
  }

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "snip-xref";
  }

  public String getDescription() {
    return "Search for snips linking to the given snip.";
  }

  public void execute(Writer writer, MacroParameter params)
    throws IllegalArgumentException, IOException {

    if (params.getLength() == 1 || params.getLength() == 2) {
      int maxHits = 10;
      if (params.getLength() == 2) {
        maxHits = Integer.parseInt(params.get("1"));
      }
      String searchString = params.get("0");

      Hits hits = null;
      try {
        hits = space.search("reference:\"" + searchString + "\"");
      } catch (Exception e) {
        Logger.warn("SnipXrefMacro: exception while searching: " + e);
      }


      if (hits != null && hits.length() > 0) {
        writer.write("<div class=\"list\"><div class=\"list-title\">references to snip ");
        writer.write(searchString);
        writer.write(": (");
        writer.write("" + hits.length());
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
          writer.write("</blockquote></div>");
        } catch (IOException e) {
          Logger.warn("I/O error while iterating over search results.");
        }
      } else {
        writer.write("Nothing found.");
      }
      AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);

      if (searchString != null && searchString.length() > 0 &&
        !SnipSpaceFactory.getInstance().exists(searchString) &&
        service.isAuthenticated(Application.get().getUser())) {
        writer.write("<p>There is no snip referencing to <b>");
        writer.write(searchString);
        writer.write("</b></p>");
      }

      return;
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
