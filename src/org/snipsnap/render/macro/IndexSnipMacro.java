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

import org.radeox.util.Linkable;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.util.collection.Collections;
import org.snipsnap.util.collection.Filterator;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that displays all Snips as an index
 *
 * @author stephan
 * @version $Id$
 */

public class IndexSnipMacro extends ListOutputMacro {
  private String[] paramDescription = {"?1: Lister to render snips"};

  public String[] getParamDescription() {
    return paramDescription;
  }


  public String getName() {
    return "index";
  }

  public String getDescription() {
    return "Displays a list of all snips of the system. Comment snips are filtered out.";
  }

  public void execute(Writer writer, SnipMacroParameter params)
    throws IllegalArgumentException, IOException {
    String type = null;
    boolean showSize = true;
    if (params.getLength() == 1) {
      type = params.get("0");
    }

    if (params.getLength() < 2) {
      final Snip snip = params.getSnipRenderContext().getSnip();
      output(writer,
             new Linkable() {
               public String getLink() {
                 return SnipLink.getSpaceRoot()+"/"+snip.getNameEncoded();
               }
             },
             "All Snips:",
             Collections.filter(SnipSpaceFactory.getInstance().getAll(),
                                new Filterator() {
                                  public boolean filter(Object obj) {
                                    String name = ((Snip) obj).getName();
                                    if (name.startsWith("comment-")) {
                                      return true;
                                    }
                                    return false;
                                  }
                                }
             ), "none written yet.", type, showSize);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
