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

import org.radeox.util.logging.Logger;
import org.snipsnap.util.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Manages the creation and finding of labels, e.g. by type.
 * Delivers a plugin structure to easily add labels.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class LabelManager {
    private Map typeMap;
    private String defaultName;
    private static LabelManager instance = null;
    private static String labelClassName = "org.snipsnap.snip.label.Label";

    private LabelManager() {
        typeMap = new HashMap();
        defaultName = "DefaultLabel";
        try {
            Iterator labelTypes = Service.providers(Class.forName(labelClassName));
            while (labelTypes.hasNext()) {
                Label label = (Label)labelTypes.next();
                addLabelType(label.getType(), label.getClass());
            }
        } catch (ClassNotFoundException e) {
            Logger.warn("LabelManager: base label type " + labelClassName + " not found. Label types have not been registered.", e);
        }
    }

    public synchronized static LabelManager getInstance() {
        if (null == instance) {
            instance = new LabelManager();
        }
        return instance;
    }

    private void addLabelType(String name, String className) {
    // TODO: check if labeltype with same name exists
    // additional parameter 'overwrite' or exception or return value?
    // (decision should to be made by user)
        try {
            Class labelClass = Class.forName(className);
            addLabelType(name, labelClass);
        } catch (ClassNotFoundException e) {
            Logger.warn("LabelManager: label class " + className + " not found and therefore not registered.", e);
        }
    }

    private void addLabelType(String name, Class labelClass) {
    // TODO: check if labeltype with same name exists
    // additional parameter 'overwrite' or exception or return value?
    // (decision should to be made by user)
        typeMap.put(name, labelClass);
    }

    public Label getLabel(String type) {
        if (null == type) { return null; }
        Class labelClass = (Class)typeMap.get(type);
        if (null == labelClass) { return null; }
        Label label = null;
        try {
            label = (Label)labelClass.newInstance();
        } catch (Exception e) {
        }
        return label;
    }

    public Label getDefaultLabel() {
        return getLabel(defaultName);
    }

    public Set getTypes() {
        return typeMap.keySet();
    }
}
