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
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.radeox.util.logging.Logger;

/**
 * Handles XML-RPC calls for the Blogger API
 * http://plant.blogger.com/api/index.html
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class BloggerHandler {
  /**
   * From the spec:
   * blogger.newPost(): Makes a new post to a designated blog. Optionally, will publish the blog after making the post.
   * appkey (string): Unique identifier/passcode of the application sending the post. (See access info.)
   * blogid (string): Unique identifier of the blog the post will be added to.
   * username (string): Login for a Blogger user who has permission to post to the blog.
   * password (string): Password for said username.
   * content (string): Contents of the post.
   * publish (boolean): If true, the blog will be published immediately after the post is made.
   **/
  public String newPost(String appkesy,
                        String blogid,
                        String username,
                        String password,
                        String content,
                        boolean publish) {
    Logger.debug("XML-RPC call to newPost()");

    UserManager um = UserManager.getInstance();
    SnipSpace space = SnipSpace.getInstance();

    User user = um.authenticate(username, password);
    if (user == null) {
      return "";
    } else {
      Application.get().setUser(user);
    }

    Snip snip = space.post(content);
    return "";
  }

  /**
   blogger.editPost: Edits a given post. Optionally, will publish the blog after making the edit.
   blogger.getUsersBlogs: Returns information on all the blogs a given user is a member of.
   blogger.getUserInfo: Authenticates a user and returns basic user info (name, email, userid, etc.).
   blogger.getTemplate: Returns the main or archive index template of a given blog.
   blogger.setTemplate: Edits the main or archive index template of a given blog.
   */
}
