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

import org.snipsnap.semanticweb.rss.Rssify;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import org.snipsnap.xmlrpc.WeblogsPing;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;

import java.sql.Date;
import java.util.Arrays;
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
  private Snip snip;

  public BlogImpl(SnipSpace space, String name) {
    this.space = space;
    this.name = name;
    this.blog = space.load(name);
  }

  public String getName() {
    return this.name;
  }

  public Snip post(String content, String title) {
    return post(BlogKit.getContent(title, content));
  }

  public Snip post(String content) {
    Date date = new Date(new java.util.Date().getTime());
    return post(content, date);
  }

  public Snip post(String content, Date date) {
    return post(blog, content, date);
  }

  public Snip post(Snip weblog, String content, Date date) {
    String snipName = name + "/" + SnipUtil.toName(date);
    Snip snip = null;

    // Should several posts per day be one snip or
    // several snips?
    if (Application.get().getConfiguration().allow(Configuration.APP_PERM_MULTIPLEPOSTS)) {
      // if (space.exists(snipName)) {
      // }
      // how many children do exist?
      // get the highest count
      // e.g. start/2003-05.06
      Snip[] existing = space.match(snipName+"/");
      int max = 1;
      System.out.println("found="+existing.length+" name="+snipName);
      for (int i = 0; i < existing.length; i++) {
        Snip post = existing[i];
        String name = post.getName();
        int index = name.lastIndexOf('/');
        System.out.println("name="+name);
        if (index != -1) {
          try {
            System.out.println("parsing="+name.substring(index+1));
            max = Math.max(Integer.parseInt(name.substring(index + 1)) + 1, max);
            System.out.println("max="+max);
          } catch (NumberFormatException e) {
            //
          }
        }
      }
      snip = snip = space.create(snipName + "/" + max, content);
    } else {
      if (space.exists(snipName)) {
        snip = space.load(snipName);
        snip.setContent(content + "\n\n" + snip.getContent());
      } else {
        snip = space.create(snipName, content);
      }
    }

    //snip.setParent(weblog);
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
    //@TODO: reduce number of posts to count;
    //@TODO: reorder snips
    List posts = space.getChildrenDateOrder(blog, count);
    posts.addAll(Arrays.asList(space.match(name + "/")));
    return posts;
  }

  public Snip getSnip() {
    return blog;
  }
}
