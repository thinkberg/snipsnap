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

import java.util.Map;

/**
 * ComplexLabel should be an example for some kind of complex label
 * this label could hold meta information about itself
 * @author Marco Mosconi
 * @version $Id$
 */
public class ComplexLabel extends BaseLabel {
    public ComplexLabel() {
        super();
        setPriority(1);
        setSupervisor("");
    }

    public ComplexLabel(String name, String value) {
        super(name, value);
        setPriority(1);
        setSupervisor("");
    }

    protected String supervisor;
    protected int priority;

    public String getType() {
        return "ComplexLabel";
    }

    public String getInputProxy() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"2\">");
        buffer.append("<tr>");
        buffer.append("<td>Label name: </td>");
        buffer.append("<td><input type=\"text\" value=\"");
        buffer.append(name);
        buffer.append("\" name=\"label.name\"/></td>");
        buffer.append("</tr><tr>");
        buffer.append("<td>Label value: </td>");
        buffer.append("<td><input type=\"text\" value=\"");
        buffer.append(value);
        buffer.append("\" name=\"label.value\"/></td>");
        buffer.append("</tr><tr>");
        buffer.append("<td>Priority: </td>");
        buffer.append("<td><select name=\"label.priority\">");
        buffer.append("<option>1</option>");
        buffer.append("<option>2</option>");
        buffer.append("<option>3</option>");
        buffer.append("<option>4</option>");
        buffer.append("</select></td>");
        buffer.append("</tr><tr>");
        buffer.append("<td>Supervisor: </td>");
        buffer.append("<td><input type=\"text\" value=\"");
        buffer.append(getSupervisor());
        buffer.append("\" name=\"label.supervisor\"/></td>");
        buffer.append("</tr></table>");
        return buffer.toString();
    }

    public void handleInput(Map input) {
        if (input.containsKey("label.name")) {
            this.name = (String)input.get("label.name");
        }
        if (input.containsKey("label.value")) {
            this.value = (String)input.get("label.value");
        }
        if (input.containsKey("label.priority")) {
            this.setPriority(Integer.valueOf((String)input.get("label.priority")).intValue());
        }
        if (input.containsKey("label.supervisor")) {
            this.setSupervisor((String)input.get("label.supervisor"));
        }
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
