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

public interface BloggerAPI extends XmlRpcHandler {
    public String getName();


    /**
     * The following methods still need to me worked on
   Blogger specific:
   blogger.getUserInfo: Authenticates a user and returns basic user info (name, email, userid, etc.).
   blogger.getTemplate: Returns the main or archive index template of a given blog.
   blogger.setTemplate: Edits the main or archive index template of a given blog.
   */

    public String newPost(String appkey,
                          String blogid,
                          String username,
                          String password,
                          String content,
                          boolean publish) throws Exception;

    /**
     * http://www.blogger.com/developers/api/1_docs/xmlrpc_editPost.html
     */
    public boolean editPost(String appkey,
                            String postId,
                                String username,
                                String password,
                                String content,
                                boolean publish) throws Exception;


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
                                String password) throws Exception;

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
                                 int numberOfPosts) throws Exception;

    /**
     * blogger.getPost
     *
     * Parameters:
     *
     * appkey : currently ignored
     * postId : postId is a unique identifier for the post created. It is the value returned by blogger.newPost. postId will look like..."zoneId|convId|pathToWeblog|msgNum".
     * username : the email address you use as a username for the site. This user must have privileges to post to the weblog as either the weblog owner, or a member of the owner group.
     * password : the password you use for the site
     *
     * Returns:
     *
     * struct containing values content ( message body ), userId, postId and dateCreated.
     **/
    public Hashtable getPost(String appkey,
                             String postId,
                             String username,
                             String password) throws Exception;

    /**
     * The blogger API document itself does not seem to reference
     * delete post but the following does:
     * http://xmlrpc.free-conversant.com/docs/bloggerAPI#deletePost
    */
    public boolean deletePost(String appkey,
                              String postId,
                              String username,
                              String password,
                              boolean publish) throws Exception;

}
