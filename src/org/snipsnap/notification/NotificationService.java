
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

package org.snipsnap.notification;

import org.snipsnap.snip.Snip;
import org.snipsnap.user.User;
import org.snipsnap.app.Application;
import org.snipsnap.notification.jabber.JabberNotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Notification service receives notifications, stores
 * them in a queue and delivers them with different notifiers,
 * e.g. JabberNotifier, IRCNotifier or MailNotitifier
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class NotificationService {
  private static NotificationService instance;

  private NotificationQueue queue;
  private Thread thread;

  private List notifiers;
  private Notifier notifier;
  private Object monitor = new Object();

  public synchronized static NotificationService getInstance() {
    if (null == instance) {
      instance = new NotificationService();
    }
    return instance;
  }

  public NotificationService() {
    queue = new NotificationQueue();
    notifiers = new ArrayList();
    //notifiers.add(new JabberNotifier("leo@snipsnap.org"));
    //notifiers.add(new JabberNotifier("funzel@snipsnap.org"));

    thread = new Thread() {
      public void run() {
        while (true) {
          while (queue.hasItems()) {
            String message = queue.remove();
            sendNotifiers(message);
          }
          try {
            synchronized(this) {
              wait();
            }
          } catch (InterruptedException e) {
            continue;
          }
        }
      }
    };
    thread.start();
  }

  public void notify(int type, Snip snip) {
    StringBuffer buffer = new StringBuffer();
    if (type ==Notification.SNIP_CREATE) {
      buffer.append("new snip '");
      buffer.append(snip.getName());
      buffer.append("'");
    }
    notify(buffer);
  }

  public void notify(int type, User user) {
    StringBuffer buffer = new StringBuffer();
    if (type == Notification.USER_LOGIN) {
      buffer.append("user login '");
      buffer.append(user.getLogin());
      buffer.append("'");
    }
    notify(buffer);
  }

  public void notify(StringBuffer buffer) {
    buffer.append(" by ");
    buffer.append(Application.get().getUser().getLogin());
    notify(buffer.toString());
  }

  public void notify(String message) {
    queue.add(message);
    synchronized (thread) {
      thread.notify();
    }
  }

  public void sendNotifiers(String message) {
    Iterator iterator = notifiers.iterator();
    while (iterator.hasNext()) {
      Notifier notifier = (Notifier) iterator.next();
      notifier.notify(message);
    }
  }
}
