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
package com.neotis.snip;

import com.neotis.snip.filter.SnipFormatter;
import com.neotis.user.Permissions;
import com.neotis.user.User;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Central class for snips.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Snip implements Ownable {
  //@TODO think about that
  public Snip parent;
  private List children;
  private Snip comment;
  private Comments comments;
  private Permissions permissions;
  private String name, content;
  private Modified modified;

  private void init() {
    if (null == children) {
      children = SnipSpace.getInstance().getChildren(this);
    }
  }

  public Snip(String name, String content) {
    this.name = name;
    this.content = content;
    this.modified = new Modified();
  }

  public static String toName(Date date) {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    return sf.format(date);
  }

  public static String toDate(String dateString) {
    SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat out = new SimpleDateFormat("EEEE, dd. MMMM yyyy");
    try {
      return out.format(in.parse(dateString));
    } catch (ParseException e) {
      return dateString;
    }
  }

  public Modified getModified() {
    return modified;
  }

  public boolean isWeblog() {
    return "start".equals(name);
  }

  public boolean isNotWeblog() {
    return ! isWeblog();
  }

  public String getOwner() {
    return getMUser();
  }

  public boolean isOwner(User user) {
    return user.getLogin().equals(getOwner());
  }

  public void addPermission(String permission, String role) {
    permissions.add(permission, role);
  }

  public void setPermissions(Permissions permissions) {
    this.permissions = permissions;
  }

  public Permissions getPermissions() {
    return permissions;
  }

  public Timestamp getCTime() {
    return modified.getcTime();
  }

  public void setCTime(Timestamp cTime) {
    this.modified.setcTime(cTime);
  }

  public Timestamp getMTime() {
    return modified.getmTime();
  }

  public void setMTime(Timestamp mTime) {
    this.modified.setmTime(mTime);
  }

  public String getCUser() {
    return modified.getcUser();
  }

  public void setCUser(User cUser) {
    this.modified.setcUser(cUser.getLogin());
  }

  public void setCUser(String cUser) {
    this.modified.setcUser(cUser);
  }

  public String getMUser() {
    return modified.getmUser();
  }

  public void setMUser(User mUser) {
    this.modified.setmUser(mUser.getLogin());
  }

  public void setMUser(String mUser) {
    this.modified.setmUser(mUser);
  }

  public List getChildren() {
    init();
    return children;
  }

  public void setComment(Snip comment) {
    this.comment = comment;
  }

  public Snip getComment() {
    return comment;
  }

  public Comments getComments() {
    if (null == comments) {
      comments = new Comments(this);
    }
    return comments;
  }

  public void addSnip(Snip snip) {
    init();
    if (!children.contains(snip)) {
      snip.setParent(this);
      children.add(snip);
      SnipSpace.getInstance().store(snip);
    }
  }

  public Snip getParent() {
    return parent;
  }

  public void setParent(Snip parentSnip) {
    if (parentSnip == this.parent) return;

    if (null != this.parent) {
      this.parent.removeSnip(this);
    }
    this.parent = parentSnip;
    parentSnip.addSnip(this);
  }

  public void removeSnip(Snip snip) {
    init();
    if (children.contains(snip)) {
      children.remove(snip);
      // snip.setParent(null);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String toXML() {
    return SnipFormatter.toXML(this, getContent());
  }

  public String getXMLContent() {
    return toXML();
  }
}