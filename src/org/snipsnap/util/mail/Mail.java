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

import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

/**
 * Mail manager to send and receive mail
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class Mail {
  public static Mail instance;

  public static synchronized Mail getInstance() {
    if (null == instance) {
      instance = new Mail();
    }
    return instance;
  }

  private Session session;

  public Mail() {
    Properties props = new Properties();
    snipsnap.api.config.Configuration config = snipsnap.api.app.Application.get().getConfiguration();
    String mailhost = config.getMailHost();
    InetAddress addr = null;
    try {
      addr = InetAddress.getByName(mailhost);
    } catch (UnknownHostException e) {
      Logger.debug("Mail: '" + mailhost + "': unknown host address");
      try {
        addr = InetAddress.getByName("mailhost");
      } catch (UnknownHostException e1) {
        try {
          addr = InetAddress.getByName("mail");
        } catch (UnknownHostException e2) {
          Logger.debug("Mail: unable to detect mail host automatically, please configure by hand");
        }
      }
    }

    if (null != mailhost) {
      props.put("mail.smtp.host", addr.getHostName());
      session = Session.getDefaultInstance(props, null);
      // session.setDebug(true);          // Verbose!
    }
  }


  public void sendMail(String sender, String recipient, String subject, String content) {
    Collection recientList = new ArrayList();
    recientList.add(recipient);
    sendMail(sender, recientList, subject, content);
  }

  public void sendMail(String recipient, String subject, String content) {
    Collection recipientList = new ArrayList();
    recipientList.add(recipient);
    //@TODO get admin mail / host
    Configuration configuration = snipsnap.api.app.Application.get().getConfiguration();
    String sender = configuration.getMailDomain();
    if (null == sender) {
      sender = configuration.getUrl();
      try {
        sender = new URL(sender).getHost();
      } catch (MalformedURLException e) {
        sender = "this-is-a-bug.org";
      }
    }
    sendMail("do-not-reply@" + sender, recipientList, subject, content);
  }

  public void sendMail(String sender, Collection recipientList, String subject, String content) {
    try {
      Message mesg = new MimeMessage(session);
      mesg.setFrom(new InternetAddress(sender));
//       InternetAddress toAddress = null;
      Iterator iterator = recipientList.iterator();
      while (iterator.hasNext()) {
        String recpt = (String) iterator.next();
        mesg.addRecipient(Message.RecipientType.TO, new InternetAddress(recpt));
      }

      // CC Address
      //InternetAddress ccAddress = new InternetAddress(message_cc);
      //mesg.addRecipient(Message.RecipientType.CC, ccAddress);
      mesg.setSubject(subject);

      // Now the message body.
      Multipart mp = new MimeMultipart();

      //BodyPart textPart = new MimeBodyPart();
      //textPart.setText(message_body);       // sets type to "text/plain"

      BodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(content, "text/html");
      mp.addBodyPart(htmlPart);
      mesg.setContent(mp);

      Transport.send(mesg);

    } catch (MessagingException ex) {
      Exception e;
      if ((e = ex.getNextException()) != null) {
        Logger.warn(ex.getMessage(), e);
      }
    }
    return;
  }
}
