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

import org.radeox.util.Linkable;
import org.radeox.util.Nameable;
import org.snipsnap.serialization.Appendable;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.User;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.io.IOException;

/**
 * Interface for snips
 *
 * @author  Stephan J. Schmidt
 * @version $Id$
 */

public interface Snip extends Linkable, Ownable, Nameable, Appendable {
  public void handle(HttpServletRequest request);

  public Access getAccess();

  public Modified getModified();

  /**
   * Returns true, when the snip is a weblog.
   * Currently only test against 'start'.
   * Should be extendet to test a "weblog"-label
   *
   * @return true, if the snip is a weblog
   */
  public boolean isWeblog();

  /**
   * Conveniance function for JSP
   *
   * @return true, if snip is not a weblog
   */
  public boolean isNotWeblog();

  public void addPermission(String permission, String role);

  public void setPermissions(Permissions permissions);

  public Permissions getPermissions();

  public String getOUser();

  public void setOUser(User oUser);

  public void setOUser(String oUser);

  public Attachments getAttachments();

  public void setAttachments(Attachments attachments);

  public Labels getLabels();

  public void setLabels(Labels labels);

  public Links getBackLinks();

  public Links getSnipLinks();

  public void setBackLinks(Links backLinks);

  public void setSnipLinks(Links snipLinks);

  public int getViewCount();

  public void setViewCount(int count);

  public int incViewCount();

  public Timestamp getCTime();

  public void setCTime(Timestamp cTime);

  public Timestamp getMTime();

  public void setMTime(Timestamp mTime);

  public String getCUser();

  public void setCUser(User cUser);

  public void setCUser(String cUser);

  public String getMUser();

  public void setMUser(User mUser);

  public void setMUser(String mUser);

  public List getChildren();

  public void setCommentedSnip(Snip comment);

  public Snip getCommentedSnip();

  public boolean isComment();

  public Comments getComments();

  public List getChildrenDateOrder();

  public List getChildrenModifiedOrder();

  /**
   * Add a child snip. Sets the parent of
   * the child to this snip and <b>stores</b> the
   * child because of the new parent.
   *
   * @param snip Snip to add as child
   */
  public void addSnip(Snip snip);

  /**
   * Removes child snip from this nsip
   *
   * @param snip Child to remove
   */
  public void removeSnip(Snip snip);

  public void setParentName(String name);

  public void setCommentedName(String name);

  public Snip getParent();

  public void setDirectParent(Snip parentSnip);

  public void setParent(Snip parentSnip);

  /**
   * Return a short version of the name.
   * Useful for vertical snip listings, where
   * the snips should not be to long.
   * End of snip name will be replaced with "..."
   *
   * @return Short name of snip
   */
  public String getShortName();

  /**
   * Return an encoded version of the name,
   * especially spaces replaced with "+"
   *
   * @return encoded name of snip
   */
  public String getNameEncoded();

  public void setName(String name);

  public String getContent();

  public void setContent(String content);

  public String getAttachmentString();

  public String toXML();

  public String getXMLContent();

  public SnipPath getPath() throws IOException;

  public String getTitle();
}
