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

import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.UserManager;

import javax.mail.*;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
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
    AppConfiguration conf = Application.get().getConfiguration();
    host = conf.getPop3Host();
    username = conf.getPop3User();
    password = conf.getPop3Password();
    mailPassword = conf.getMailBlogPassword();
    if (null == host || null == username || null == password || null == mailPassword) {
      active = false;
      System.err.println("PostDaemon: not started");
    } else {
      active = true;
      System.err.println("PostDaemon: started");
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

        for (int i = 0, n = message.length; i < n; i++) {
          StringWriter writer = new StringWriter();

          System.err.println(i + ": " + message[i].getFrom()[0]
              + "\t" + message[i].getSubject());
          System.err.println(message[i].getContentType());

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
                processImage(writer, message[i]);
              } else if (contentType.startsWith("multipart/")) {
                // process multipart message
                processMultipart(writer, (Multipart) message[i].getContent());
              }

              Application.get().setUser(UserManager.getInstance().load("stephan"));
              SnipSpace.getInstance().post(writer.getBuffer().toString(), title);
            } catch (Exception e) {
              System.err.println("PostDaemon Error:" + e.getMessage());
              e.printStackTrace();
            } finally {
              // Delete message, either because we processed it or we couldn't
              // process it
              message[i].setFlag(Flags.Flag.DELETED, true);
            }
          }
        }
// Close connection
        folder.close(true);
        store.close();
      } catch (Exception e) {
        System.err.println("PostDaemon Error:" + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public void processImage(Writer writer, Part part) throws MessagingException, IOException {
    writer.write("{image:");
    writer.write(part.getFileName());
    writer.write("}");
  }

  public void processMultipart(Writer writer, Multipart mp) throws MessagingException {
    for (int j = 0; j < mp.getCount(); j++) {
      try {
      Part part = mp.getBodyPart(j);
      System.err.println("Disposition=" + part.getDisposition());
      String contentType = part.getContentType();
      System.err.println("content-type=" + contentType);
      System.err.println("Object=" + part);
        if (contentType.startsWith("text/plain")) {
          writer.write((String) part.getContent());
        } else if (contentType.startsWith("image/")) {
          processImage(writer, part);
        } else if (contentType.startsWith("multipart/")) {
          processMultipart(writer, (Multipart) part.getContent());
        }
      } catch (Exception e) {
        System.err.println("PostDaemon: Error reading message: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}
