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
 * SnipLabel connects a Snip to another Snip (should it be possible to reference more than one Snip?)
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class SnipLabel extends BaseLabel {
    public SnipLabel() {
        super();
    }

    public SnipLabel(String name, String value) {
        super(name, value);
    }

    public String getType() {
        return "SnipLabel";
    }

    public String getInputProxy() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"2\">");
        buffer.append("<tr>");
        buffer.append("<td>Link label: </td>");
        buffer.append("<td><input type=\"text\" value=\"");
        buffer.append(name);
        buffer.append("\" name=\"label.name\"/></td>");
        buffer.append("</tr><tr>");
        buffer.append("<td>Link target (Snip): </td>");
        buffer.append("<td><input type=\"text\" value=\"");
        buffer.append(value);
        buffer.append("\" name=\"label.value\"/></td>");
        buffer.append("</tr></table>");
        return buffer.toString();
    }

    public String getSnipLink() {
        // for now, assume value is exactly the linked Snip name ...
        return value;
    }

    public String[] getSnipLinks() {
        // for now, assume value is exactly the linked Snip name ...
        return new String[] { value };
    }

}
