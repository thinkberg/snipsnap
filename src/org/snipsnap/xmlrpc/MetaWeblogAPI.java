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
 * Handles XML-RPC calls for the MetaWeblog API
 * http://www.xmlrpc.com/metaWeblogApi
 * also used the following enhancement
 * http://www.xmlrpc.com/metaWeblogNewMediaObject
 *
 * Some design ideas taken from Blojsom, thanks.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public interface MetaWeblogAPI extends BloggerAPI {

  public Vector getRecentPosts(String blogid,
                               String username,
                               String password,
                               int numberOfPosts) throws Exception;

  /**
   * http://www.xmlrpc.com/stories/storyReader$2509#threeEntrypoints
   * @param blogid
   * @param username
   * @param password
   * @param struct
   * @param publish
   * @return
   * @throws Exception
   */
  public String newPost(String blogid,
                        String username,
                        String password,
                        Hashtable struct,
                        boolean publish) throws Exception;

    /**
     *  http://www.xmlrpc.com/stories/storyReader$2509#threeEntrypoints
     * @param postId
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
  public Hashtable getPost(String postId,
                           String username,
                           String password) throws Exception;

  /**
   * yes. the metaweblog api takes a struct for the content, as opposed
   * to the Blogger API that just takes a string.
   *  http://www.xmlrpc.com/stories/storyReader$2509#threeEntrypoints
   * @param postId
   * @param username
   * @param password
   * @param content
   * @param publish
   * @return
   * @throws Exception
   */
  public boolean editPost(String postId,
                              String username,
                              String password,
                              Hashtable content,
                              boolean publish) throws Exception;

    /**
     * http://www.xmlrpc.com/metaWeblogNewMediaObject
     * That document does not specify how one deletes an attachment. I will assume
     * this is done by sending the same request but with an empty body of bytes.
     * @param blogid
     * @param username
     * @param password
     * @param content
     * @return
     * @throws Exception
     */
   public Hashtable  newMediaObject(String blogid,
                                   String username,
                                   String password,
                                   Hashtable content) throws Exception;


    /*  These methods were suggested for addition to the official metaweblog api on May 7, 2003.
        http://www.blogger.com/developers/api/1_docs/

      They belong to the blogger api and the metaweblog api should be backward compatible with it.
     (If so the perhaps this class should just extend blogger api?)

     -- this one is useful: metaWeblog.deletePost (appkey, postid, username, password, publish) returns boolean
     metaWeblog.getTemplate (appkey, blogid, username, password, templateType) returns string
     metaWeblog.setTemplate (appkey, blogid, username, password, template, templateType) returns boolean
   metaWeblog.getUsersBlogs (appkey, username, password) returns array
   */



}
