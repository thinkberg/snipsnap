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

package com.neotis.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * User roles
 *
 * @author stephan
 * @version $Id$
 */

public class Roles {
  private Set roles;

  private static Set ROLES = new TreeSet();

  private static Set getAllRoles() {
    if (ROLES == null) {
      ROLES.add("Editor");
      ROLES.add("NoComment");
    }
    return ROLES;
  }

  public Roles(String roleString) {
    this.roles = deserialize(roleString);
  }

  public Roles() {
    roles = new HashSet();
  }

  public Roles(Set roleSet) {
    this.roles = new HashSet(roleSet);
  }

  public void add(String role) {
    roles.add(role);
  }

  public void addAll(Roles roles) {
    this.roles.addAll(roles.getRoleSet());
  }

  public Iterator iterator() {
    return roles.iterator();
  }

  public boolean contains(String role) {
    return roles.contains(role);
  }

  public boolean containsAny(Roles r1) {
    // Optimize to use the smaller set
    Roles r2 = this;
    Iterator iterator = r1.iterator();
    while (iterator.hasNext()) {
      String s = (String) iterator.next();
      if (r2.contains(s)) return true;
    }
    return false;
  }

  public Set getRoleSet() {
    return Collections.unmodifiableSet(roles);
  }

  public String toString() {
    return serialize(roles);
  }

  private String serialize(Set roles) {
    if (null == roles || roles.isEmpty()) return "";

    StringBuffer buffer = new StringBuffer();
    Iterator iterator = roles.iterator();
    while (iterator.hasNext()) {
      String role = (String) iterator.next();
      buffer.append(role);
      if (iterator.hasNext()) buffer.append(":");
    }
    return buffer.toString();
  }

  private Set deserialize(String roleString) {
    if (null == roleString || "".equals(roleString)) return new HashSet();

    StringTokenizer st = new StringTokenizer(roleString, ":");
    Set roles = new HashSet();

    while (st.hasMoreTokens()) {
      roles.add(st.nextToken());
    }

    return roles;
  }
}
