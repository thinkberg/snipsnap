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

import org.snipsnap.xmlrpc.WeblogsPing;
import org.snipsnap.user.Roles;
import org.snipsnap.user.Permissions;
import org.snipsnap.semanticweb.rss.Rssify;

import java.sql.Date;
import java.util.List;

/**
 * BlogImpl for Blog.
 *
 * @author stephan
 * @version $Id$
 */

public class BlogImpl implements Blog {
  private String name;
  private Snip blog;
  private SnipSpace space;

  public BlogImpl(SnipSpace space, String name) {
    this.space = space;
    this.name = name;
    this.blog = space.load(name);
  }

  public static String getPostName() {
    Date date = new Date(new java.util.Date().getTime());
    return SnipUtil.toName(date);
  }

  public static String getContent(String title, String content) {
    return content = "1 " + title + " {anchor:" + title + "}\n" + content;
  }

  public String getName() {
    return this.name;
  }

  public Snip post(String content, String title) {
    return post(getContent(title, content));
  }

  public Snip post(String content) {
    Date date = new Date(new java.util.Date().getTime());
    return post(content, date);
  }

  public Snip post(String content, Date date) {
    return post(blog, content, date);
  }

  public Snip post(Snip weblog, String content, Date date) {
    String name = SnipUtil.toName(date);
    Snip snip = null;
    if (space.exists(name)) {
      snip = space.load(name);
      snip.setContent(content + "\n\n" + snip.getContent());
    } else {
      snip = space.create(name, content);
    }
    snip.setParent(weblog);
    snip.addPermission(Permissions.EDIT, Roles.OWNER);
    space.store(snip);

    // Ping weblogs.com that we changed our site
    WeblogsPing.ping(weblog);
    return snip;
  }

  public List getFlatPosts() {
    return Rssify.rssify(getPosts(10));
  }

  public List getPosts(int count) {
    return space.getChildrenDateOrder(blog, count);
  }

  public Snip getSnip() {
    return blog;
  }
}
