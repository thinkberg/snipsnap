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

import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import org.snipsnap.notification.jabber.JabberNotifier;
import snipsnap.api.snip.Snip;
import snipsnap.api.user.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Central Message Queue
 * Could be moved to a mor IoC Style with SignalSlot. Could
 * be extended to provide easier intra-container communication
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class MessageService {
  private List consumers;

  public MessageService() {
    consumers = new ArrayList();
  }

  public synchronized void send(Message message) {
    Iterator iterator = consumers.iterator();
    while (iterator.hasNext()) {
      Consumer consumer = (Consumer) iterator.next();
      consumer.consume(message);
    }
  }

  public synchronized void register(Consumer consumer) {
    if (! consumers.contains(consumer)) {
      consumers.add(consumer);
    }
  }

  public synchronized void deregister(Consumer consumer) {
    if (consumers.contains(consumer)) {
      consumers.add(consumer);
    }
  }
}
