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

/**
 * TypeLabel assigns a type to a Snip
 * @author Marco Mosconi
 * @version $Id$
 */
public class TypeLabel extends BaseLabel {
    public TypeLabel() {
        name = "type";
        value = "";
    }

    public TypeLabel(String value) {
        this.name = "type";
        this.value = value;
    }

    public String getType() {
        return "TypeLabel";
    }

    public String getInputProxy() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<input type=\"hidden\" value=\"");
        buffer.append(name);
        buffer.append("\" name=\"label.name\"/>");
        buffer.append("Type: ");
        buffer.append("<input type=\"text\" value=\"");
        buffer.append(value);
        buffer.append("\" name=\"label.value\"/>");
        return buffer.toString();
    }

    public String getListProxy() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<td>");
        buffer.append("Type");
        buffer.append("</td><td>");
        buffer.append(value);
        buffer.append("</td>");
        return buffer.toString();
    }

    public void setName(String name) {
        // name should not be set manually
    }
}
