/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002-2004 Stephan J. Schmidt, Matthias L. Jugel
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

import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.snip.label.Label;
import org.snipsnap.container.Components;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/*
 * Macro that displays all Snips with a certain label
 *
 * @author stephan
 * @version $Id$
 */

public class LabelSearchMacro extends ListOutputMacro {
  private String[] paramDescription =
     {"1: labels that should be searched"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "label-search";
  }

  public String getDescription() {
    return "Show all snips that have a certain label.";
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {

    // {label-search:Category=Snip}
    // Map pairs = params.getParams();
    String type = params.get("type");
    String name = params.get("name");
    String value = params.get("value");

    SnipSpace snipspace = (SnipSpace) Components.getComponent(SnipSpace.class);
    List snipList = snipspace.getAll();

    List result = new ArrayList();

    Iterator iterator = snipList.iterator();
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      Labels labels = snip.getLabels();
      boolean noLabelsAll = labels.getAll().isEmpty();

      if (!noLabelsAll) {
        Collection LabelsCat;
// Search for all type labels
        Label label = labels.getLabel(name);
        if (label != null && label.getValue().equals(value)) {
          result.add(snip);
        }
      }
    }

//    String type = null;
//    boolean showSize = true;
//    if (params.getLength() > 1) {
//        type = params.get("1");
//    }
//
    if (params.getLength() > 1) {
      output(writer, "snips with category:", result, "none found.", type, true);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
