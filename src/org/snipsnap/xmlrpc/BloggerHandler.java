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

import org.snipsnap.user.AuthenticationService;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Handles XML-RPC calls for the Blogger API
 * http://xmlrpc.free-conversant.com/docs/bloggerAPI
 * http://plant.blogger.com/api/index.html
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class BloggerHandler extends XmlRpcSupport implements BloggerAPI {
  public static final String API_PREFIX = "blogger";
  private MetaWeblogHandler metaHandler;

  public BloggerHandler(AuthenticationService authenticationService, MetaWeblogHandler metaHandler) {
     this.metaHandler = metaHandler;
  }

  public String getName() {
    return API_PREFIX;
  }

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
                        boolean publish) throws Exception {
        return metaHandler.newPost(appkey,
                blogid,
                username,
                password,
                content,
                publish);
  }

    public boolean editPost(String appkey,
                            String postId,
                            String username,
                            String password,
                            String content,
                            boolean publish) throws Exception {
        return metaHandler.editPost(appkey,
                postId,
                username,
                password,
                content,
                publish);
    }

  public Vector getUsersBlogs(String appkey,
                              String username,
                              String password) throws Exception {
       return metaHandler.getUsersBlogs(appkey,
              username,
              password);
 }

  public Vector getRecentPosts(String appkey,
                               String blogid,
                               String username,
                               String password,
                               int numberOfPosts) throws Exception {
      return metaHandler.getRecentPosts(appkey,
              blogid,
              username,
              password,
              numberOfPosts);
/*
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
 */
  }


  public Hashtable getPost(String appkey,
                           String postId,
                           String username,
                           String password) throws Exception {

     return  metaHandler.getPost(appkey,
              postId,
              username,
              password);
    /*
    User user = authenticate(username, password);
    Snip snip = SnipSpaceFactory.getInstance().load(postId);
    Hashtable post = new Hashtable();
    post.put("content", snip.getXMLContent());
    post.put("userid", snip.getOUser());
    post.put("postid", snip.getName());
    post.put("dateCreated", snip.getCTime());
    return post;
    */
  }

    /**
     *
     * @param appkey
     * @param postId
     * @param username
     * @param password
     * @param publish
     * @return
     * @throws Exception
     */

    public boolean deletePost(String appkey,
                              String postId,
                              String username,
                              String password,
                              boolean publish) throws Exception {
        return metaHandler.deletePost(appkey,
                postId,
                username,
                password,
                publish);
    }

}
