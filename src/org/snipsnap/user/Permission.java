/*            Compent
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
package org.snipsnap.user;

/**
 * Permission to do something.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class Permission {
  public final static Permission EDIT_SNIP = new Permission("EDIT_SNIP");
  public final static Permission ATTACH_TO_SNIP = new Permission("ATTACH_TO_SNIP");
  public final static Permission POST_TO_SNIP = new Permission("POST_TO_SNIP");
  public final static Permission CREATE_SNIP = new Permission("CREATE_SNIP");
  public final static Permission REMOVE_SNIP = new Permission("REMOVE_SNIP");
  public final static Permission VIEW_SNIP = new Permission("VIEW_SNIP");
  public final static Permission POST_COMMENT = new Permission("POST_COMMENT");
  public final static Permission EDIT_COMMENT = new Permission("EDIT_COMMENT");
  public final static Permission LOCK_SNIP = new Permission("LOCK_SNIP");
  public final static Permission VIEW_ATTACHMENTS = new Permission("VIEW_ATTACHMENTS");
  public final static Permission VIEW_LABELS = new Permission("VIEW_LABELS");

  private String permission;

  public Permission(String permission) {
    this.permission = permission;
  }

  public int hashCode() {
    return permission.hashCode();
  }

  public boolean equals(Object obj) {
    return permission.equals(obj);
  }

  public String toString() {
    return permission;
  }
}

