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
package org.snipsnap.snip;

import org.snipsnap.snip.filter.SnipFormatter;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.User;
import org.snipsnap.util.Nameable;

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
public class Snip implements Ownable, Nameable {
  //@TODO think about that
  public Snip parent;
  private List children;
  private Snip comment;
  private Comments comments;
  private Permissions permissions;
  private String name, content;
  private Modified modified;
  private String oUser;
  private Access access;
  private Labels labels;


  private void init() {
    if (null == children) {
      children = SnipSpace.getInstance().getChildren(this);
    }
  }

  public Snip(String name, String content) {
    this.name = name;
    this.content = content;
    this.modified = new Modified();
    this.access = new Access(this);
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

  public Access getAccess() {
    return access;
  }

  public Modified getModified() {
    return modified;
  }

  public boolean isWeblog() {
    return "start".equals(name);
  }

  public boolean isNotWeblog() {
    return !isWeblog();
  }

  public String getOwner() {
    return getCUser();
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

  public String getOUser() {
    return oUser;
  }

  public void setOUser(String oUser) {
    this.oUser = oUser;
  }

  public Labels getLabels() {
    return labels;
  }

  public void setLabels(Labels labels) {
    this.labels = labels;
  }

  public Links getBackLinks() {
    return access.getBackLinks();
  }

  public Links getSnipLinks() {
    return access.getSnipLinks();
  }

  public void setBackLinks(Links backLinks) {
    access.setBackLinks(backLinks);
  }

  public void setSnipLinks(Links snipLinks) {
    access.setSnipLinks(snipLinks);
  }

  public int getViewCount() {
    return access.getViewCount();
  }

  public int incViewCount() {
    return access.incViewCount();
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

  public void setCommentedSnip(Snip comment) {
    this.comment = comment;
  }

  public Snip getCommentedSnip() {
    return comment;
  }

  public boolean isComment() {
    return !(null == comment);
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

  public String getShortName() {
    String name = getName();
    if (name.length()>20) {
      name = name.substring(0,20-3) + "...";
    }
    return name;
  }

  public String getNameEncoded() {
    try {
      return SnipLink.encode(getName());
    } catch (Exception e) {
      return getName();
    }
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
    try {
      return toXML();
    } catch (Exception e) {
      e.printStackTrace();
      return "<span class=\"error\">" + e + "</span>";
    }
  }
}
