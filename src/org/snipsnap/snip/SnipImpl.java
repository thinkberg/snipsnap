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

import org.snipsnap.app.Application;
import org.snipsnap.snip.filter.SnipFormatter;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.User;
import org.snipsnap.interceptor.Aspects;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.List;

/**
 * Central class for snips.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class SnipImpl implements Snip {
  //@TODO think about that
  public Snip parent;
  private List children;
  private Snip comment;
  private Attachments attachments;
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

  public SnipImpl(String name, String content) {
    this.name = name;
    this.content = content;
    this.modified = new Modified();
    this.access = new Access(this);
  }

  public Access getAccess() {
    return access;
  }

  public Modified getModified() {
    return modified;
  }

  /**
   * Returns true, when the snip is a weblog.
   * Currently only test against "start".
   * Should be extendet to test a "weblog"-label
   *
   * @return true, if the snip is a weblog
   */
  public boolean isWeblog() {
    return "start".equals(name);
  }

  /**
   * Conveniance function for JSP
   *
   * @return true, if snip is not a weblog
   */
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

  public void setOUser(User oUser) {
    this.oUser = oUser.getLogin();
  }

  public void setOUser(String oUser) {
    this.oUser = oUser;
  }

  public Attachments getAttachments() {
    return attachments;
  }

  public void setAttachments(Attachments attachments) {
    this.attachments = attachments;
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

  public void setViewCount(int count) {
    access.setViewCount(count);
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

  /**
   * Add a child snip. Sets the parent of
   * the child to this snip and <b>stores</b> the
   * child because of the new parent.
   *
   * @param snip Snip to add as child
   */
  public void addSnip(Snip snip) {
    init();
    if (!children.contains(snip)) {
      snip.setParent(this);
      children.add(snip);
      SnipSpace.getInstance().systemStore(snip);
    }
  }

  /**
   * Removes child snip from this nsip
   *
   * @param snip Child to remove
   */
  public void removeSnip(Snip snip) {
    init();
    if (children.contains(snip)) {
      children.remove(snip);
      // snip.setParent(null);
    }
  }

  public Snip getParent() {
    return parent;
  }

  /**
   * Directly sets the parent snip, does
   * not add the snip to the parent.
   * This is needed for restoring from storage.
   * Better solution wanted
   *
   * @param parentSnip new parent snip of this snip
   */
  public void setDirectParent(Snip parentSnip) {
     this.parent = parentSnip;
  }

  public void setParent(Snip parentSnip) {
    if (parentSnip == this.parent) return;

    if (null != this.parent) {
      this.parent.removeSnip(this);
    }
    this.parent = parentSnip;
    parentSnip.addSnip(this);
  }

  public String getName() {
    return name;
  }

  /**
   * Return a short version of the name.
   * Useful for vertical snip listings, where
   * the snips should not be to long.
   * End of snip name will be replaced with "..."
   *
   * @return Short name of snip
   */
  public String getShortName() {
    return SnipLink.cutLength(getName(), 20);
  }

  /**
   * Return an encoded version of the name,
   * especially spaces replaced with "+"
   *
   * @return encoded name of snip
   */
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
    long start = Application.get().start();
    //String xml = SnipFormatter.toXML((Snip) Aspects.getThis(), getContent());
    String xml = SnipFormatter.toXML(this, getContent());
    Application.get().stop(start, "Formatting " + name);
    return xml;
  }

  public String getXMLContent() {
    String tmp = null;
    try {
      tmp = toXML();
    } catch (Exception e) {
      tmp = "<span class=\"error\">" + e + "</span>";
      e.printStackTrace();
    } catch (Error err) {
      err.printStackTrace();
      tmp = "<span class=\"error\">" + err + "</span>";
    }

    return tmp;
  }

  public Writer appendTo(Writer s) throws IOException {
    s.write(getXMLContent());
    return s;
  }
}