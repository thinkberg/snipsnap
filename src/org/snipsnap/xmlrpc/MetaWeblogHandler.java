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

package org.snipsnap.xmlrpc;

import org.snipsnap.user.User;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.Blog;
import org.apache.xmlrpc.XmlRpcException;
import org.radeox.util.logging.Logger;

import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;

/**
 * Handles XML-RPC calls for the MetaWeblog API
 * http://www.xmlrpc.com/metaWeblogApi
 *
 * Some design ideas taken from Blojsom, thanks.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class MetaWeblogHandler extends XmlRpcSupport {
  public static final String API_PREFIX = "metaWeblog";

  public String getName() {
    return API_PREFIX;
  }

  public Vector getRecentPosts(String blogid,
                               String username,
                               String password,
                               int numberOfPosts) throws XmlRpcException {
    Logger.debug("XML-RPC call to getRecentPosts()");

    User user = authenticate(username, password);
    Snip snip = SnipSpaceFactory.getInstance().getBlog().getSnip();

    List children =
        SnipSpaceFactory.getInstance().getChildrenDateOrder(snip, numberOfPosts);

    Vector posts = new Vector(children.size());
    for (Iterator i = children.iterator(); i.hasNext();) {
      Snip each = (Snip) i.next();
      Hashtable data = new Hashtable();
      data.put("userid", each.getOUser() == null ? "" : each.getOUser());
      data.put("dateCreated", each.getCTime());
      data.put("content", each.getContent());
      data.put("postid", each.getName());
      posts.add(data);
    }
    return posts;
  }

  /**
   metaWeblog.newPost (blogid, username, password, struct, publish) returns string
   metaWeblog.editPost (postid, username, password, struct, publish) returns true
   metaWeblog.getPost (postid, username, password) returns struct
   */

  public String newPost(String blogid, String username, String password, Hashtable struct, boolean publish) throws Exception {
    User user = authenticate(username, password);

    Blog blog  = SnipSpaceFactory.getInstance().getBlog();

    Hashtable postcontent = struct;

    String title = (String) postcontent.get("title");
    String content = (String) postcontent.get("description");

    Snip snip = null;
    if (null == title) {
       snip = blog.post(content);
    } else {
      snip = blog.post(content, title);
    }

    return snip.getName();
  }

  public Hashtable getPost(String postId,
                           String username,
                           String password) throws XmlRpcException {
    User user = authenticate(username, password);
    Snip snip = SnipSpaceFactory.getInstance().load(postId);
    Hashtable post = new Hashtable();
    post.put("content", snip.getXMLContent());
    post.put("userid", snip.getOUser());
    post.put("postid", snip.getName());
    post.put("dateCreated", snip.getCTime());
    return post;
  }
}
