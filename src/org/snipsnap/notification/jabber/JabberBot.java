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

package org.snipsnap.notification.jabber;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;

/**
 * Bot which communicates via Jabber IM. This Bot can
 * receive and send jabber group and private messages.
 *
 * @author Stephan J.Schmidt
 * @version $Id$
 */

public class JabberBot {
  private static JabberBot instance;
  private XMPPConnection con;

  public static synchronized JabberBot getInstance() {
    if (null == instance) {
      instance = new JabberBot();
    }
    return instance;
  }

  public JabberBot() {
    try {
      con = new XMPPConnection("snipsnap.org");
      con.login("snipbot", "snipbot");
//      JabberRosterService roster = session.getRosterService();
//      roster.addToRoster("funzel@snipsnap.org", "Funzel", "SnipSnap", false);
//      roster.addToRoster("leo@snipsnap.org", "Leo", "SnipSnap", false);

      // session.getPresenceService().setToAvailable(null,null);
    } catch (Exception e) {
      Logger.warn("Unable to start JabberBot", e);
    }
  }

  public void send(String user, String message) {
    try {
      Logger.debug("Sending '" + message + "' to '" + user + "' ...");
      Chat chat = con.createChat(user);
      Message newMessage = chat.createMessage();
      newMessage.setBody("message!");
      newMessage.setSubject(Application.get().getConfiguration().getName());
      chat.sendMessage(newMessage);
      Logger.debug("Sent.");
    } catch (Exception e) {
      Logger.warn("Unable to send message to " + user, e);
    }
  }
}
