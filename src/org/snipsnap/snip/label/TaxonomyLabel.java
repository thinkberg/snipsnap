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

package org.snipsnap.snip.label;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.label.BaseLabel;
import org.snipsnap.snip.label.Label;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.AuthenticationService;

/**
 *
 * Taxonomy labels link category-snips to form a taxonomy.
 * The label links to another snip which is the "parent" of this snip
 * like OO (parent) -> Java (this)
 *
 * @author Franziska Wohl
 * @version $Id$
 */
public class TaxonomyLabel extends BaseLabel {

  public TaxonomyLabel() {
    name = "Taxonomy";
    value = "";
  }

  public TaxonomyLabel(String value) {
    this();
    this.value = value;
  }

  /**
   * Type of label
   *
   * @return
   */
  public String getType() {
    return "TaxonomyLabel";
  }

  /* Ausgabe der Category-List
   */
  public String getInputProxy() {
    StringBuffer buffer = new StringBuffer();
    SnipSpace snipspace = (SnipSpace) Components.getComponent(SnipSpace.class);
    List snipList = snipspace.getAll();

    buffer.append("Taxonomy: ");

// Should be refactored to a TaxonomyBase Label (see Category Label)
    Iterator iterator = snipList.iterator();
    buffer.append("<select name=\"label.value\" size=\"1\">");
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      Labels labels = snip.getLabels();
      boolean noLabelsAll = labels.getAll().isEmpty();
      if (!noLabelsAll) {
        Collection LabelsCat;
        LabelsCat = labels.getLabels("TypeLabel");
        if (!LabelsCat.isEmpty()) {
          Iterator iter = LabelsCat.iterator();
          while (iter.hasNext()) {
            Label label = (Label) iter.next();
            if (label.getValue().equals("Category")) {
              String category = snip.getNameEncoded();
              buffer.append("<option>");
              buffer.append(category);
              buffer.append("</option>");
            }
          }
        }
      }
    }
    buffer.append("</select>");

    return buffer.toString();
  }

  public String getListProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<td>");
    buffer.append("Parent:");
    buffer.append("</td><td>");
    getSnipLink(buffer, this.value);
    buffer.append("</td><td>");
    return buffer.toString();
  }

  private StringBuffer getSnipLink(StringBuffer buffer, String name) {
    AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);

    if (SnipSpaceFactory.getInstance().exists(name)) {
      SnipLink.appendLink(buffer, name, name);
    } else if (!service.isAuthenticated(Application.get().getUser())) {
      buffer.append(name);
    } else {
      SnipLink.appendCreateLink(buffer, name);
    }
    return buffer;
  }
}
