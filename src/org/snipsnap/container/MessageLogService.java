/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002-2004 Stephan J. Schmidt, Matthias L. Jugel
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

package org.snipsnap.container;

import org.snipsnap.notification.Consumer;
import org.snipsnap.notification.Message;
import org.snipsnap.notification.MessageService;


/**
 * Component that listens to a MessageService and
 * logs all messages.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class MessageLogService implements Consumer, Component {
  private MessageService messageService;

  public MessageLogService(MessageService messageService) {
    this.messageService = messageService;
    messageService.register(this);
  }

  public void consume(Message message) {
    System.out.println("Message: "+message.toString());
  }
}
