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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manages the creation and finding of labels, e.g. by type.
 * Delivers a plugin structure to easily add labels.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class LabelManager {
  private Map typeMap;
  private String defaultName;

  public static LabelManager instance;

  public synchronized static LabelManager getInstance() {
    if (null == instance) {
      instance = new LabelManager();
    }
    return instance;
  }

  private void addLabelType(String name, String className) {
    try {
      Class labelClass = Class.forName(className);
      typeMap.put(name, labelClass);
    } catch (ClassNotFoundException e) {
      System.err.println("LabelManager: label " + className + " not found " + e.getMessage());
    }
    return;
  }

  private LabelManager() {
    typeMap = new HashMap();
    defaultName = "SnipLabel";
    addLabelType("SnipLabel", "org.snipsnap.snip.label.SnipLabel");
    return;
  }

  public Label getLabel(String type) {
    if (null == type) return null;
    Class labelClass = (Class) typeMap.get(type);
    if (null == labelClass) return null;
    Label label = null;
    try {
      label = (Label) labelClass.newInstance();
    } catch (Exception e) {
    }
    return label;
  }

  public Label getDefaulLabel() {
    return getLabel(defaultName);
  }

  public Set getTypes() {
    return typeMap.keySet();
  }


}
