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
import org.radeox.util.i18n.ResourceManager;
import snipsnap.api.app.Application;
import org.snipsnap.container.Components;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.snip.SnipSpace;
import snipsnap.api.snip.SnipSpaceFactory;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.components.SearchService;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;

/*
 * Macro for xref searches in SnipSnap. The macro
 * displays the snips reference to the snip for the input string.
 *
 * @author Mario Ivankovits
 * @version $Id$
 */

public class SnipXrefMacro extends BaseMacro {
  private SearchService searchService;

  public SnipXrefMacro() {
    searchService = (SearchService) Components.getComponent(SearchService.class);
  }

  public String getName() {
    return "snip-xref";
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.snipxref.params").split(";");
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.snipxref.description");
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
        hits = searchService.search("reference:\"" + searchString + "\"");
      } catch (Exception e) {
        Logger.warn("SnipXrefMacro: exception while searching: " + e);
      }


      if (hits != null && hits.length() > 0) {
        writer.write("<div class=\"list\"><div class=\"list-title\">");
        MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "macro.snipxref.title"));
        writer.write(mf.format(new Object[] { searchString, new Integer(hits.length()) }));
        writer.write("</div>");

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
        !snipsnap.api.snip.SnipSpaceFactory.getInstance().exists(searchString) &&
        service.isAuthenticated(Application.get().getUser())) {
        MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "macro.snipxref.norefs"));
        writer.write("<p>");
        writer.write(mf.format(searchString));
        writer.write("</p>");
      }

      return;
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
