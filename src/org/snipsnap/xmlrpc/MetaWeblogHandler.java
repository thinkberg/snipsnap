/**
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

import org.apache.xmlrpc.XmlRpcException;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.net.FileUploadServlet;
import org.snipsnap.semanticweb.rss.BlogFeeder;
import org.snipsnap.snip.Blog;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.attachment.Attachment;
import org.snipsnap.snip.attachment.storage.AttachmentStorage;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Security;
import org.snipsnap.user.User;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import org.snipsnap.snip.BlogKit;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.feeder.Feeder;

/**
 * Handles XML-RPC calls for the MetaWeblog API
 * http://www.xmlrpc.com/metaWeblogApi
 * <p/>
 * Some design ideas taken from Blojsom, thanks.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class MetaWeblogHandler extends XmlRpcSupport implements MetaWeblogAPI {
    public String API_PREFIX = "metaWeblog";

    public MetaWeblogHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public String getName() {
        return API_PREFIX;
    }

    ///
    /// Blogger API methods
    ///

    public String newPost(String appkey,
                          String blogid,
                          String username,
                          String password,
                          String body,
                          boolean publish) throws Exception {
        Hashtable content = new Hashtable(1);
        content.put("description", body);
        return newPost(blogid, username, password, content, publish);
    }

    public boolean editPost(String appkey,
                            String postId,
                            String username,
                            String password,
                            String body,
                            boolean publish) throws Exception {
        Hashtable content = new Hashtable(1);
        content.put("description", body);
        return editPost(postId, username, password, content, publish);
    }

    public Vector getUsersBlogs(String appkey, String username, String password) throws Exception {
        User user = authenticate(username, password);

        Hashtable blog = new Hashtable(3);
        blog.put("url", Application.get().getConfiguration().getUrl());
        blog.put("blogid", "0");
        blog.put("blogName", Application.get().getConfiguration().getName());
        Vector vector = new Vector(1);
        vector.add(blog);
        return vector;
    }

    public Hashtable getPost(String appkeyIgnored,
                             String postId,
                             String username,
                             String password) throws Exception {
        return getPost(postId, username, password);
    }

    public boolean deletePost(String appkey,
                              String postId,
                              String username,
                              String password,
                              boolean publish) throws Exception {
        User user = authenticate(username, password);
        Snip snip = SnipSpaceFactory.getInstance().load(postId);
        SnipSpaceFactory.getInstance().remove(snip);
        return true;
    }

    public Vector getRecentPosts(String ignoredAppkey,
                                 String postId,
                                 String username,
                                 String password,
                                 int numberOfPosts) throws Exception {
        return getRecentPosts(postId, username, password, numberOfPosts);
    }



    ///
    /// MetaWeblog API methods
    ///


    public String newPost(String blogid, String username, String password, Hashtable postcontent, boolean publish) throws Exception {
        User user = authenticate(username, password);

        Blog blog = SnipSpaceFactory.getInstance().getBlog(blogid);

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
        post.put("userid", snip.getOUser());  //todo: why does getOUser work here but not with RSSSnip? (see getRecentPosts())
        post.put("postid", snip.getName());  //todo: remove? I could not find postid in spec, should be guid, no?
        post.put("dateCreated", snip.getCTime());//todo: remove? I could not 'dateCreated' in spec. Should be 'pubDate' no?

        post.put("pubDate", snip.getCTime());
        post.put("title", snip.getTitle()); //can name be null?
        post.put("link", (snip.getLink() == null) ? "" : snip.getLink());  //todo: why does RssSnip return null?

        post.put("description", snip.getContent());
        post.put("content", snip.getXMLContent()); //todo: verify. I could not find 'content' in the spec. (is in rss jsp though)
        post.put("guid", snip.getName());

        try {
            post.put("comments", snip.getComments().getPostUrl());
        } catch (IOException e) {
            //todo: ignore or log?
            //todo: This exception does not look like it should be thrown to start off with.
            //todo: how can getting a url throw an io exception?
        }

        return post;
    }

    public boolean editPost(String postId, String username, String password, Hashtable content, boolean publish) throws Exception {
        User user = authenticate(username, password);
        SnipSpace space = SnipSpaceFactory.getInstance();
        Snip snip = space.load(postId);
        String title = (String) content.get("title");
        String body = (String) content.get("description");
        //could deal with "dateCreated" later
        snip.setContent(BlogKit.getContent(title, body));
        space.systemStore(snip);
        return true;
    }


    public Vector getRecentPosts(String blogid,
                                 String username,
                                 String password,
                                 int numberOfPosts) throws Exception {
        User user = authenticate(username, password);

        Feeder feeder = new BlogFeeder(blogid);
        List children = feeder.getFeed();

        Vector posts = new Vector(children.size());
        for (Iterator i = children.iterator(); i.hasNext() && numberOfPosts-- > 0;) {
            Snip snip = (Snip) i.next();
            Hashtable data = new Hashtable();
            //todo: decide what the policy for null values should be.
            //todo: will require deciding (then comenting) what the postconditions of
            //todo: the Snip methods is.
            data.put("userid", snip.getCUser()); //todo: verify  getOUser did not work with RSSSnips
            data.put("title", snip.getTitle()); //can name be null?
            data.put("link", (snip.getLink() == null) ? "" : snip.getLink());  //todo: why does RssSnip return null?
            data.put("dateCreated", snip.getCTime());//todo: remove? I could not find 'dateCreated' in spec. Should be 'pubDate' no?

            data.put("pubDate", snip.getCTime());
            data.put("description", snip.getContent());
            data.put("content", snip.getXMLContent()); //todo: verify. I could not find 'content' in the spec.
            //todo: getName() for RSSSnip does not return the same as for the snip returned by newPost().
            data.put("guid", snip.getName());
            try {
                data.put("comments", snip.getComments().getPostUrl());
            } catch (IOException e) {
                //todo: ignore or log?
                //todo: This exception does not look like it should be thrown to start off with.
                //todo: how can getting a url throw an io exception?
            }
            posts.add(data);
        }
        return posts;
    }

    //todo: need comment about the "postid" data
    public Hashtable newMediaObject(String blogid,
                                    String username,
                                    String password,
                                    Hashtable content) throws Exception {
        User user = authenticate(username, password);
        System.out.println("new media object.");

        byte[] data = (byte[]) content.get("bits");
        String fileName = (String) content.get("name");
        String mime = (String) content.get("key");
        String snipId = (String) content.get("postid");
        System.out.println("the postid is: "+snipId);

        SnipSpace space = SnipSpaceFactory.getInstance();
        Snip snip = space.load(snipId);

        AttachmentStorage attachmentStorage = (AttachmentStorage) Components.getComponent(AttachmentStorage.class);

        if (!Security.checkPermission(Permissions.ATTACH_TO_SNIP, user, snip)) {
            throw new XmlRpcException(0, "Do not have write access to this snip");
            //todo: on inspecting the checkPersmissions code I had the fleeting
            //impression that everyone will have permission to attach to snips. verify.
        }

        //upload file
        try {
            if (data.length == 0) {//then we have to delete this attachment
              Attachments attachments = snip.getAttachments();
              Attachment attachment = attachments.getAttachment(fileName);
                if (null != attachment) {
                  attachmentStorage.delete(attachment);
                  attachments.removeAttachment(attachment);
                }
              // make sure to store the changed snip
              SnipSpaceFactory.getInstance().store(snip);
              Hashtable result = new Hashtable(1);
              result.put("url", "");
              return result;
            }
            fileName = FileUploadServlet.getCanonicalFileName(fileName);
            if (snipId != null && snipId.length() > 0 &&
                    mime != null && mime.length() > 0) {
                Attachment attachment =
                        new Attachment(fileName,
                                mime, 0, new Date(),
                                snip.getName());
                OutputStream out = attachmentStorage.getOutputStream(attachment);
                out.write(data);
                out.flush();
                out.close();
   //todo: what happens if the file name allready exists? verify.
                attachment.setSize(data.length);
                snip.getAttachments().addAttachment(attachment);

                SnipSpaceFactory.getInstance().store(snip);
                Hashtable result = new Hashtable(1);
                // this most probably does not return an url
                result.put("url", attachment.getLocation());//does this return a url?
                //todo: find out what else one can return
                return result;

            }

        } catch (IOException e) {
            //TODO: check that 0 is an acceptable code
            throw new XmlRpcException(0, "Error writing file to disk: " + e);
        }
        return null;
    }


}
