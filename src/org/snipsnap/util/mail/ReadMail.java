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

package org.snipsnap.util.mail;

import javax.mail.*;
import java.util.Properties;

/**
 * Read mails from e.g. Pop3 for remote blogging
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class ReadMail {
  public static void main(String[] args) {
    String host = "tanis.first.fhg.de";
    String username = "SnipSnap/hq, mgmt";
    String password = "SMB2003";

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
      folder.open(Folder.READ_ONLY);

// Get directory
      Message message[] = folder.getMessages();

      for (int i = 0, n = message.length; i < n; i++) {
        System.out.println(i + ": " + message[i].getFrom()[0]
            + "\t" + message[i].getSubject());
      }

// Close connection
      folder.close(false);
      store.close();
    } catch (MessagingException e) {
      System.err.println("Error:"+e.getMessage());
      e.printStackTrace();
    }
  }
}
