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
 * User roleSet
 *
 * @author stephan
 * @version $Id$
 */

public class Roles {
  private Set roleSet;

  public final static String AUTHENTICATED = "Authenticated";
  public final static String OWNER = "Owner";
  public final static String EDITOR = "Editor";
  public final static String NOCOMMENT = "NoComment";

  private static Set ROLES = null;

  public static Set allRoles() {
    if (ROLES == null) {
      ROLES = new TreeSet();
      ROLES.add(EDITOR);
      ROLES.add(NOCOMMENT);
      ROLES = Collections.unmodifiableSet(ROLES);
    }
    return ROLES;
  }

  public Set getAllRoles() {
    return Roles.allRoles();
  }

  public Roles(Roles roles) {
    roleSet = new HashSet(roles.roleSet);
  }

  public Roles(String roleString) {
    this.roleSet = deserialize(roleString);
  }

  public Roles() {
    roleSet = new HashSet();
  }

  public Roles(Set roleSet) {
    this.roleSet = new HashSet(roleSet);
  }

  public boolean isEmpty() {
    return roleSet.isEmpty();
  }

  public void remove(String role) {
    if (roleSet.contains(role)) {
      roleSet.remove(role);
    }
  }

  public void add(String role) {
    roleSet.add(role);
  }

  public void addAll(Roles roles) {
    this.roleSet.addAll(roles.getRoleSet());
  }

  public Iterator iterator() {
    return roleSet.iterator();
  }

  public boolean contains(String role) {
    return roleSet.contains(role);
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
    return Collections.unmodifiableSet(roleSet);
  }

  public String toString() {
    return serialize(roleSet);
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

  public boolean equals(Roles obj) {
    return getRoleSet().equals(obj.getRoleSet());
  }
}
