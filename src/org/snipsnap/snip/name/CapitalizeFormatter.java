package org.snipsnap.snip.name;

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


/**
 * Formatter that capitalizes the name
 *
 * @author stephan
 * @version $Id$
 */

public class CapitalizeFormatter implements NameFormatter {
  // Null Object Pattern
  private NameFormatter parent = new NoneFormatter();

  public void setParent(NameFormatter parent) {
    this.parent = parent;
  }

  public String format(String name) {
    String parentName =  parent.format(name);
    return parentName.substring(0,1).toUpperCase() + parentName.substring(1);
  }
}
