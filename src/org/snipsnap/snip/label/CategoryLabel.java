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

import snipsnap.api.app.Application;
import org.snipsnap.container.Components;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.snip.SnipSpace;
import snipsnap.api.snip.SnipSpaceFactory;
import org.snipsnap.snip.label.BaseLabel;
import snipsnap.api.label.Label;
import snipsnap.api.label.Labels;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.util.URLEncoderDecoder;

/**
 * Label which categoriezes snips
 *
 * @author Franziska Wohl
 * @version $id$
 */
public class CategoryLabel extends BaseLabel {

  private String type = "CategoryLabel";

  public CategoryLabel() {
    name = "Category";
    value = "";
  }

  public CategoryLabel(String value) {
    this();
    this.value = value;
  }

  /**
   * Return type of Label
   * @return
   */
  public String getType() {
    return type;
  }

  /**
   * Return a html input form fragment to
   * input label data. See visual proxy pattern
   *
   * @return
   */
  public String getInputProxy() {
    StringBuffer buffer = new StringBuffer();
    snipsnap.api.snip.SnipSpace snipspace = (SnipSpace) Components.getComponent(SnipSpace.class);
    List snipList = snipspace.getAll();

    buffer.append("Category: ");
    Iterator iterator = snipList.iterator();
    buffer.append("<select name=\"label.value\" size=\"1\">");
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      Labels labels = snip.getLabels();
      boolean noLabelsAll = labels.getAll().isEmpty();

      if (!noLabelsAll) {
        Collection LabelsCat;
// Search for all type labels
        LabelsCat = labels.getLabels("TypeLabel");
        if (!LabelsCat.isEmpty()) { //true = leer
          Iterator iter = LabelsCat.iterator();
          while (iter.hasNext()) {
// We only want snips with a label Type:Category
            Label label = (snipsnap.api.label.Label) iter.next();
            if (label.getValue().equals("Category")) {
              String category = snip.getName();
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
    buffer.append("Category:");
    buffer.append("</td><td>");
    getSnipLink(buffer, value);
    buffer.append("</td><td>");
    return buffer.toString();
  }

  /*
   * Create s snipLink, should probably moved around
   */
  private StringBuffer getSnipLink(StringBuffer buffer, String name) {
    AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);

    if (SnipSpaceFactory.getInstance().exists(name)) {
      String[] strings = name.split("/");
      String string = strings[strings.length - 1];
      SnipLink.appendLink(buffer, name, string);
    } else if (!service.isAuthenticated(snipsnap.api.app.Application.get().getUser())) {
      buffer.append(name);
    } else {
      SnipLink.appendCreateLink(buffer, name);
    }
    return buffer;
  }
}
