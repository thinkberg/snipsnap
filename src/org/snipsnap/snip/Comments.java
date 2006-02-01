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

import org.radeox.util.i18n.ResourceManager;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import snipsnap.api.app.Application;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.snip.SnipSpaceFactory;
import snipsnap.api.config.Configuration;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Handler for comments added to snips.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Comments {
  private Snip snip;
  private List comments;
  private snipsnap.api.snip.SnipSpace space;
  private Set users;

  public Comments(Snip snip) {
    this.snip = snip;
    space = SnipSpaceFactory.getInstance();
  }

  /**
   * Lazy initialization of containers
   * (comments, users)
   */
  private void init() {
    if (null == comments) {
      comments = SnipSpaceFactory.getInstance().getComments(snip);
    }

    if (null == users) {
      users = new HashSet();
      Iterator iterator = comments.iterator();
      while (iterator.hasNext()) {
        Snip snip = (Snip) iterator.next();
        users.add(snip.getCUser());
      }
    }
  }

  /**
   * Get list of all comments for the snip
   *
   * @return List of comments (snips)
   */
  public List getComments() {
    init();
    return comments;
  }

  /**
   * Post a new comment to snip
   *
   * @param content Content of the comment
   * @return The generated comment (snip)
   */
  public Snip postComment(String content) {
    init();
    String name = "comment-" + snip.getName() + "-" + (getCount() + 1);
    Snip comment = space.create(name, content);
    comment.setCommentedSnip(this.snip);
    comment.addPermission(Permissions.EDIT_SNIP, Roles.OWNER);
    space.store(comment);
    comments.add(comment);
    users.add(comment.getCUser());
    return comment;
  }

  /**
   * Returns a pretty printed version of the comments
   * for the snip.
   * (usernames, count)
   *
   * @return Pretty printed version of comments
   */
  public String getCommentString() {
    StringBuffer buffer = new StringBuffer();
    if (getCount() > 0) {
      MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "comments.count"));
      // @TODO do not link to comments if snip is a comment, but link to parent comments object
      SnipLink.appendLinkWithRoot(buffer, SnipLink.getCommentsRoot(),
              SnipLink.encode(snip.getName()),
              mf.format(new Object[]{new Integer(getCount())}));
      buffer.append(" ");
      MessageFormat mfBy = new MessageFormat(ResourceManager.getString("i18n.messages", "comments.by"));
      buffer.append(mfBy.format(new Object[]{getUserString()}));
    } else {
      buffer.append(ResourceManager.getString("i18n.messages", "comments.none"));
    }

    return buffer.toString();
  }

  public String getPostUrl() throws IOException {
    Configuration config = Application.get().getConfiguration();
    return SnipLink.getCommentsRoot() + "/" + snip.getNameEncoded() + "#post";
  }

  public String getPostString() {
    StringBuffer buffer = new StringBuffer();
    SnipLink.appendLinkWithRoot(buffer,
            SnipLink.getCommentsRoot(),
            snip.getNameEncoded() + "#post",
            ResourceManager.getString("i18n.messages", "comments.post"));
    return buffer.toString();
  }

  /**
   * Append user list "funzel, arte, warg" to
   * buffer.
   */
  public String getUserString() {
    init();
    StringBuffer buffer = new StringBuffer();
    Iterator userIterator = users.iterator();
    while (userIterator.hasNext()) {
      String s = (String) userIterator.next();
      SnipLink.appendLink(buffer, s);
      if (userIterator.hasNext()) {
        buffer.append(", ");
      }
    }
    return buffer.toString();
  }

  /**
   * Get number of comments
   *
   * @return Number of comments
   */
  public int getCount() {
    init();
    return comments.size();
  }

  public String toString() {
    return getCommentString();
  }
}
