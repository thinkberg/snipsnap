/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This pr  ogram is free software; you can redistribute it and/or
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

package org.snipsnap.util.mail;

import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.snip.BlogKit;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.UserManager;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Automatically reads post from a source (e.g. Pop3) and
 * posts them to a weblog (or Snip).
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class PostDaemon {
  private static PostDaemon instance;
  private String host, username, password;
  private String mailPassword;
  private Timer pop3Timer;
  private boolean active;

  public static synchronized PostDaemon getInstance() {
    if (null == instance) {
      instance = new PostDaemon();
    }
    return instance;
  }

  public PostDaemon() {
    Configuration conf = Application.get().getConfiguration();
    host = conf.getMailPop3Host();
    username = conf.getMailPop3User();
    password = conf.getMailPop3Password();
    mailPassword = conf.getMailBlogPassword();
    if (null == host || null == username || null == password || null == mailPassword) {
      active = false;
      Logger.warn("PostDaemon: not started");
    } else {
      active = true;
      Logger.warn("PostDaemon: started");
      pop3Timer = new Timer();
      pop3Timer.schedule(new TimerTask() {
        public void run() {
          process();
        }
      }, 1 * 10 * 1000, 1 * 10 * 1000);
    }
  }

  public void process() {
    if (active) {
      try {
        // Create empty properties
        Properties props = new Properties();

// Get session
        Session session = Session.getDefaultInstance(props, null);

// Get the store
        Store store = session.getStore("pop3");
        store.connect(host, username, password);

// Get folder
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

// Get directory
        Message message[] = folder.getMessages();

        String name = BlogKit.getPostName();

        for (int i = 0, n = message.length; i < n; i++) {
          StringWriter writer = new StringWriter();

          Logger.debug(i + ": " + message[i].getFrom()[0]
              + "\t" + message[i].getSubject());
          Logger.debug(message[i].getContentType());

          Address sender = message[i].getFrom()[0];
          String title = message[i].getSubject();
          if (title != null && title.startsWith(mailPassword)) {
            // only correct sender
            // cut password from title
            title = title.substring(mailPassword.length()).trim();

            try {
              String contentType = message[i].getContentType();
              if (contentType.startsWith("text/plain")) {
                writer.write((String) message[i].getContent());
              } else if (contentType.startsWith("image/")) {
                processImage(writer, message[i], name);
              } else if (contentType.startsWith("multipart/")) {
                // process multipart message
                processMultipart(writer, (Multipart) message[i].getContent(), name);
              }

              // BUG
              String user = Application.get().getConfiguration().getAdminLogin();
              Application.get().setUser(UserManager.getInstance().load(user));
              SnipSpaceFactory.getInstance().getBlog().post(writer.getBuffer().toString(), title);
            } catch (Exception e) {
              Logger.warn("PostDaemon Error:", e);
            } finally {
              // Delete message, either because we processed it or we couldn't
              // process it
              message[i].setFlag(Flags.Flag.DELETED, true);
            }
          } else {
            Logger.warn("PostDaemon: Wrong password.");
          }
        }
// Close connection
        folder.close(true);
        store.close();
      } catch (Exception e) {
        Logger.warn("PostDaemon Error", e);
      }
    }
  }

  public void processImage(Writer writer, Part part, String name) throws MessagingException, IOException {
    writer.write("{image:");
    writer.write(part.getFileName());
    storeImage(part, name);
    writer.write("}");
  }

  public void processMultipart(Writer writer, Multipart mp, String name) throws MessagingException {
    for (int j = 0; j < mp.getCount(); j++) {
      try {
      Part part = mp.getBodyPart(j);
      Logger.debug("Disposition=" + part.getDisposition());
      String contentType = part.getContentType();
      Logger.debug("content-type=" + contentType);
      Logger.debug("Object=" + part);
        if (contentType.startsWith("text/plain")) {
          writer.write((String) part.getContent());
        } else if (contentType.startsWith("image/")) {
          processImage(writer, part, name);
        } else if (contentType.startsWith("multipart/")) {
          processMultipart(writer, (Multipart) part.getContent(), name);
        }
      } catch (Exception e) {
        Logger.warn("PostDaemon: Error reading message", e);
      }
    }
  }

  public void storeImage(Part part, String name) {
    try {
      if (part != null  && part.getFileName() != null) {
        Configuration config = Application.get().getConfiguration();
        File imageDir = new File(config.getFile().getParentFile().getParentFile(), "images");
        File file = new File(imageDir, "image-" + name + "-" + part.getFileName());
        Logger.debug("Uploading '" + part.getFileName() + "' to '" + file.getAbsolutePath() + "'");
        FileOutputStream out = new FileOutputStream(file);
        InputStream in = part.getInputStream();
        byte[] buf = new byte[4096];
        int length = 0;
        while ((length = in.read(buf)) != -1) {
          out.write(buf, 0, length);
        }
        out.close();
        in.close();
      } else {
        Logger.warn("PostDaemon: Error processing mail");
      }
    } catch (IOException e) {
      Logger.warn("PostDaemon: Error processing mail", e);
    } catch (MessagingException e) {
      Logger.warn("PostDaemon: Error processing mail", e);
    }

  }
}
