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


package org.snipsnap.render.macro;

import org.apache.lucene.search.Hits;
import org.radeox.macro.Macro;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.Snip;
import org.snipsnap.user.UserManager;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro for fulltext searches in SnipSnap. The macro
 * displays the search results for the input string. Can be
 * used in snips to "store" searches. For user defined
 * searches use a {field} macro combined with the {search}
 * macro.
 *
 * @author stephan
 * @version $Id$
 */

public class SnipTreeMacro extends Macro {
  private SnipSpace space;

  private String[] paramDescription =
     {"1: Namespace prefix"};

  public SnipTreeMacro() {
    space = SnipSpaceFactory.getInstance();
  }

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "snip-tree";
  }

  public String getDescription() {
    return "Show a tree of snips from the namespace.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params.getLength() == 1) {
      Snip[] snips = space.match(params.get("0"));

      writer.write("<ul>");
      for (int i = 0; i < snips.length; i++) {
        Snip snip = snips[i];
        writer.write("<li>");
        writer.write(snip.getName());
        writer.write("</li>");
      }
      writer.write("</ul>");
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
