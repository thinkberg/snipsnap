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

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.radeox.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

/**
 * Handles XML-RPC calls for the Blogger API
 * http://xmlrpc.free-conversant.com/docs/bloggerAPI
 * http://plant.blogger.com/api/index.html
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class BloggerHandler extends XmlRpcSupport {
  /**
   * From the spec:
   * blogger.newPost(): Makes a new post to a designated blog. Optionally, will publish the blog after making the post.
   *
   * appkey (string): Unique identifier/passcode of the application sending the post. (See access info.)
   * blogid (string): Unique identifier of the blog the post will be added to.
   * username (string): Login for a Blogger user who has permission to post to the blog.
   * password (string): Password for said username.
   * content (string): Contents of the post.
   * publish (boolean): If true, the blog will be published immediately after the post is made.
   *
   * @param appkey Application key, currently not used by SnipSnap
   * @param blogid Identifaction for the blog, currenty SnipSnap supports only one weblog
   * @param username Login of a SnipSnap user whit permission to post to weblog
   * @param password Password credential
   * @param content Content of the post, currently no HTML
   * @param publish SnipSnap currently does not support post drafts
   *
   * @return name Name of the post
   */
  public String newPost(String appkey,
                        String blogid,
                        String username,
                        String password,
                        String content,
                        boolean publish) throws XmlRpcException {
    Logger.debug("XML-RPC call to newPost()");

    SnipSpace space = SnipSpaceFactory.getInstance();

    User user = authenticate(username, password);

    Snip snip = space.post(content);
    return snip.getName();
  }

  /**
   * From the spec:
   * blogger.getUsersBlogs: Returns information on all the blogs a given user is a member of.
   * appkey (string): Unique identifier/passcode of the application sending the post. (See access info.)
   * username (string): Login for a Blogger user who has permission to post to the blog.
   * password (string): Password for said username.
   *
   * @param appkey Application key, currently not used by SnipSnap
   * @param username Login of a SnipSnap user whit permission to post to weblog
   * @param password Password credential
   *
   * @return bloglist List of Blogs, currently SnipSnap has only one weblog
   **/
  public Vector getUsersBlogs(String appkey,
                              String username,
                              String password) throws XmlRpcException {
    Logger.debug("XML-RPC call to getUserBlogs()");

    User user = authenticate(username, password);

    Hashtable blog = new Hashtable(3);
    blog.put("url", Application.get().getConfiguration().getUrl());
    blog.put("blogid", "0");
    blog.put("blogName", Application.get().getConfiguration().getName());
    Vector vector = new Vector(1);
    vector.add(blog);
    return vector;
  }

  /**
   * From the spec:
   * Returns an array of structs containing the latest n posts to a given blog, newest first.
   * appkey (string): Unique identifier/passcode of the application sending the post. (See access info.)
   * blogid (string): Unique identifier of the blog the post will be added to.
   * username (string): Login for a Blogger user who has permission to post to the blog.
   * password (string): Password for said username.
   * numberOfPosts (int): Number of posts to retrieve.
   *
   * @param appkey Application key, currently not used by SnipSnap
   * @param blogid Identifaction for the blog, currenty SnipSnap supports only one weblog
   * @param username Login of a SnipSnap user whit permission to post to weblog
   * @param password Password credential
   * @param numberOfPosts Number of the posts to retrieve
   *
   * @return recentPosts List of recents weblog posts
   **/
  public Vector getRecentPosts(String appkey,
                               String blogid,
                               String username,
                               String password,
                               int numberOfPosts) throws XmlRpcException {
    Logger.debug("XML-RPC call to getRecentPosts()");

    User user = authenticate(username, password);
    Snip snip = SnipSpaceFactory.getInstance().load("start");

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
  blogger.getPost

  Parameters:

      * appkey : currently ignored
      * postId : postId is a unique identifier for the post created. It is the value returned by blogger.newPost. postId will look like..."zoneId|convId|pathToWeblog|msgNum".
      * username : the email address you use as a username for the site. This user must have privileges to post to the weblog as either the weblog owner, or a member of the owner group.
      * password : the password you use for the site

   Returns:

      * struct containing values content ( message body ), userId, postId and dateCreated.
  **/

  public Hashtable getPost(String appkey,
                           String postId,
                           String username,
                           String password) throws XmlRpcException {
    Logger.debug("XML-RPC call to getRecentPosts()");

    User user = authenticate(username, password);
    Snip snip = SnipSpaceFactory.getInstance().load(postId);
    Hashtable post = new Hashtable();
    post.put("content", snip.getXMLContent());
    post.put("userid", snip.getOUser());
    post.put("postid", snip.getName());
    post.put("dateCreated", snip.getCTime());
    return post;
  }

  /**
   blogger.editPost: Edits a given post. Optionally, will publish the blog after making the edit.

   Blogger specific:
   blogger.getUserInfo: Authenticates a user and returns basic user info (name, email, userid, etc.).
   blogger.getTemplate: Returns the main or archive index template of a given blog.
   blogger.setTemplate: Edits the main or archive index template of a given blog.
   */
}
