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
package com.neotis.jsp;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

public class SnipBean {
  Snip snip = null;

  HttpServletRequest request = null;
  String name, content;

  public String getModified() {
    return snip.getModified();
  }

  public String getComments() {
    return snip.getComments().getCommentString();
  }

  public Timestamp getCTime() {
    return snip.getCTime();
  }

  public Timestamp getMTime() {
    return snip.getMTime();
  }

  public void setName(String name) {
    System.err.println("setName(" + name + ")");
    this.name = name;
    snip = SnipSpace.getInstance().load(name);
  }

  public String getName() {
    if (snip != null) {
      return snip.getName();
    } else {
      return name != null ? name : "";
    }
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    if (snip != null) {
      return snip.getContent();
    }
    return content != null ? content : "";
  }


  public String getXMLContent() {
    System.err.println("getXMLContent()");
    if (snip != null) {
      return snip.toXML();
    }
    return "";
  }

}
