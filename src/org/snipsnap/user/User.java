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

import java.util.Set;
import java.util.HashSet;

/**
 * User class.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class User {

  private String login;
  private String passwd;
  private String email;
  private String status;
  private Set roles;

  public User(String login, String passwd, String email) {
    this.login = login;
    this.passwd = passwd;
    this.email = email;
  }

  public void setStatus(String status) {
    this.status = status;
    return;
  }

  public String getStatus() {
    return status;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswd() {
    return passwd;
  }

  public String getLogin() {
    return login;
  }

  public void setRoles(Set roles) {
    this.roles = roles;
    return;
  }

  public Set getRoles() {
    if (null == roles) {
      roles = new HashSet();
    }
    return roles;
  }
}