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

import org.picocontainer.PicoContainer;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.interceptor.Aspects;
import org.snipsnap.render.context.SnipRenderContext;
import org.snipsnap.snip.attachment.Attachment;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.snip.label.RenderEngineLabel;
import org.snipsnap.snip.name.CapitalizeFormatter;
import org.snipsnap.snip.name.NameFormatter;
import org.snipsnap.snip.name.PathRemoveFormatter;
import org.snipsnap.snip.storage.SnipSerializer;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.User;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Central class for snips.
 *
 * TODO: cUser, mUser, cTime, ... -> modified to composite object
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class SnipImpl implements Snip {
  private NameFormatter nameFormatter;
  private String applicationOid;

  //@TODO think about that
  public Snip parent;
  private List children;
  private Snip comment;
  private Comments comments;
  private String name, content;
  private String oUser;

  // @TODO: Composite Object
  private Permissions permissions;
  private Access access;
  private Labels labels;
  private Attachments attachments;
  private Modified modified;
  private int version = 1;

  // @TODO: Remove
  private String commentedName;
  private String parentName;

  private void init() {
    if (null == children) {
      children = SnipSpaceFactory.getInstance().getChildren((Snip) Aspects.getThis());
    }
  }

  public SnipImpl(String name, String content) {
    this.name = name;
    this.content = content;
    this.modified = new Modified();
    this.access = new Access();
  }

  public void handle(HttpServletRequest request) {
    access.handle(name, request);
    SnipSpaceFactory.getInstance().delayedStore((Snip) Aspects.getThis());
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public void setApplication(String applicationOid) {
    this.applicationOid = applicationOid;
  }

  public String getApplication() {
    return applicationOid;
  }

  public Access getAccess() {
    return access;
  }

  public Modified getModified() {
    return modified;
  }

  /**
   * Returns true, when the snip is a weblog.
   * Currently only test against 'start'.
   * Should be extendet to test a "weblog"-label
   *
   * @return true, if the snip is a weblog
   */
  public boolean isWeblog() {
    return content.indexOf("{weblog}") != -1;
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
    if (null != commentedName && !"".equals(commentedName) && null == comment) {
      comment = SnipSpaceFactory.getInstance().load(commentedName);
    }
    return comment;
  }

  public boolean isComment() {
    return !(null == getCommentedSnip());
  }

  public Comments getComments() {
    if (null == comments) {
      comments = new Comments((Snip) Aspects.getThis());
    }
    return comments;
  }

  /**
   * Get a list of child snips, ordered by date with
   * the newest one first
   *
   * @return List of child snips
   */
  public List getChildrenDateOrder() {
    return SnipSpaceFactory.getInstance().getChildrenDateOrder((Snip) Aspects.getThis(), 10);
  }

  public List getChildrenModifiedOrder() {
    return SnipSpaceFactory.getInstance().getChildrenModifiedOrder((Snip) Aspects.getThis(), 10);
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
      snip.setParent((Snip) Aspects.getThis());
      children.add(snip);
      SnipSpaceFactory.getInstance().systemStore(snip);
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
    if (null != parentName && !"".equals(parentName) && null == parent) {
      parent = SnipSpaceFactory.getInstance().load(parentName);
    }
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

  public void setParentName(String name) {
    this.parentName = name;
  }

  public String getParentName() {
    return this.parent == null ? parentName : this.parent.getName();
  }

  public void setCommentedName(String name) {
    this.commentedName = name;
  }

  public String getCommentedName() {
    return this.parent == null ? commentedName : this.comment.getName();
  }

  public void setParent(Snip parentSnip) {
    if (parentSnip == this.parent) {
      return;
    }

    if (null != this.parent) {
      this.parent.removeSnip((Snip) Aspects.getThis());
    }
    this.parent = parentSnip;
    parentSnip.addSnip((Snip) Aspects.getThis());
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

  public String getLink() {
    return SnipLink.createLink(this.name);
  }

  public String getAttachmentString() {
    StringBuffer tmp = new StringBuffer();
    Iterator it = attachments.iterator();
    File fileStorePath = new File(Application.get().getConfiguration().getFileStore(), "snips");
    while (it.hasNext()) {
      Attachment att = (Attachment) it.next();
      File file = new File(fileStorePath, att.getLocation());
      if (file.exists()) {
        tmp.append(SnipLink.createLink(SnipLink.getSpaceRoot() + "/" + SnipLink.encode(name), att.getName(), att.getName()));
        tmp.append(" (").append(att.getSize()).append(")");
        if (it.hasNext()) {
          tmp.append("<br/> ");
        }
      } else {
        Logger.log(Logger.WARN, file.getAbsolutePath() + " is missing");
      }
    }
    return tmp.toString();
  }

  public String toXML() {
    //long start = Application.get().start();
    PicoContainer container = Components.getContainer();

    RenderEngineLabel reLabel =
      (RenderEngineLabel) getLabels().getLabel("RenderEngine");
    RenderEngine engine = null;
    if (reLabel != null) {
      try {
        engine = (RenderEngine) container.getComponentInstance(Class.forName(reLabel.getValue()));
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

    // make sure we get a render engine
    if (null == engine) {
      engine = (RenderEngine) container.getComponentInstance(Components.DEFAULT_ENGINE);
    }


    RenderContext context = new SnipRenderContext(
      (Snip) Aspects.getThis(),
      (SnipSpace) container.getComponentInstance(SnipSpace.class));
    context.setParameters(Application.get().getParameters());

    String xml = "";
    // should the engine be set by the engine to the context?
    xml = engine.render(content, context);
    //Logger.debug(getName() + " is cacheable: " + context.isCacheable());
    //String xml = SnipFormatter.toXML(this, getContent());
    //Application.get().stop(start, "Formatting " + name);
    return xml;
  }

  public String getXMLContent() {
    String tmp = null;
    try {
      tmp = toXML();
    } catch (Exception e) {
      tmp = "<span class=\"error\">" + e + "</span>";
      e.printStackTrace();
      Logger.warn("SnipImpl: unable to get XMLContent", e);
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

  public SnipPath getPath() {
    return new SnipPath(this);
  }

  public String getTitle() {
    if (null == nameFormatter) {
      nameFormatter = new CapitalizeFormatter();
      nameFormatter.setParent(new PathRemoveFormatter());
    }
    return nameFormatter.format(name);
  }

  public int hashCode() {
    return name.hashCode();
  }

//  public String toString() {
//    return "{name="+getName()+", parent="+parent+", @"+hashCode()+"}";
//  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Snip)) {
      return false;
    }

    return ((Snip) obj).getName().equals((this.name));
  }

  public String toString() {
    return getName();
  }

  public Snip copy(String newName) {
    SnipSpace space = (SnipSpace)Components.getComponent(SnipSpace.class);
    Snip newSnip = space.create(newName, getContent());
    newSnip.setLabels(getLabels());

    List atts = getAttachments().getAll();
    Iterator attsIt = atts.iterator();
    File fileStorePath = new File(Application.get().getConfiguration().getFileStore(), "snips");
    while(attsIt.hasNext()) {
      Attachment oldAtt = (Attachment)attsIt.next();
      Attachment att = new Attachment(oldAtt.getName(), oldAtt.getContentType(), oldAtt.getSize(), oldAtt.getDate(), oldAtt.getLocation());
      String location = att.getLocation();
      File attFile = new File(fileStorePath, location);
      if(attFile.exists()) {
        try {
          File newLocation = new File(newSnip.getName(), attFile.getName());
          File newAttFile = new File(fileStorePath, newLocation.getPath());
          newAttFile.getParentFile().mkdirs();
          BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newAttFile));
          BufferedInputStream in = new BufferedInputStream(new FileInputStream(attFile));
          byte buf[] = new byte[4096];
          int length = -1;
          while ((length = in.read(buf)) != -1) {
            out.write(buf, 0, length);
          }
          out.flush();
          out.close();
          in.close();
          att.setLocation(newLocation.getPath());
        } catch (IOException e) {
          Logger.warn("SnipImpl: unable to copy attachment: "+attFile, e);
        }
      } else {
        newSnip.getAttachments().removeAttachment(att);
      }
    }
    return newSnip;
  }
}
