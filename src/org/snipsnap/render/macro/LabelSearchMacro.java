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

import org.radeox.util.i18n.ResourceManager;
import org.snipsnap.container.Components;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpace;
import snipsnap.api.label.Label;
import snipsnap.api.label.Labels;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
 * Macro that displays all Snips with a certain label
 *
 * @author stephan
 * @version $Id$
 */

public class LabelSearchMacro extends ListOutputMacro {
  public String getName() {
    return "label-search";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.labelsearch.description");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.labelsearch.params").split(";");
  }

  public void execute(Writer writer, SnipMacroParameter params)
          throws IllegalArgumentException, IOException {

    // {label-search:Category=Snip}
    // Map pairs = params.getParams();
    String type = params.get("type");
    String name = params.get("name");
    String value = params.get("value");

    SnipSpace snipspace = (snipsnap.api.snip.SnipSpace) Components.getComponent(SnipSpace.class);
    List snipList = snipspace.getAll();

    List result = new ArrayList();

    Iterator iterator = snipList.iterator();
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      snipsnap.api.label.Labels labels = snip.getLabels();
      boolean noLabelsAll = labels.getAll().isEmpty();

      if (!noLabelsAll) {
        Collection LabelsCat;
// Search for all type labels
        Label label = labels.getLabel(name, value);
        if (label != null) {
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
      MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "macro.labelsearch.title"),
                                           ResourceManager.getLocale("i18n.messages"));
      output(writer, params.getSnipRenderContext().getSnip(),
             mf.format(new Object[]{name, value}), result,
             ResourceManager.getString("i18n.messages", "macro.labelsearch.notfound"), type, true);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
