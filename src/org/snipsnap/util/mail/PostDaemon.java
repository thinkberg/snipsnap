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
          System.err.println(i + ": " + message[i].getFrom()[0]
              + "\t" + message[i].getSubject());
          if (message[i].getContentType().startsWith("text/plain")) {
            try {
              // only correct sender
              Address sender = message[i].getFrom()[0];
              String content = (String) message[i].getContent();
              String title = message[i].getSubject();
              if (title != null && title.startsWith(mailPassword)) {
                // cut password from title
                title = title.substring(mailPassword.length()).trim();
                Application.get().setUser(UserManager.getInstance().load("stephan"));
                SnipSpace.getInstance().post(content, title);
              }
            } catch (Exception e) {
              System.err.println("PostDaemon: Error reading message: " + e.getMessage());
              e.printStackTrace();
            } finally {
              message[i].setFlag(Flags.Flag.DELETED, true);
            }
          }
        }

// Close connection
        folder.close(true);
        store.close();
      } catch (MessagingException e) {
        System.err.println("Error:" + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}
