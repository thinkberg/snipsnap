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
package org.snipsnap.snip;

import org.apache.xmlrpc.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;
import org.snipsnap.snip.storage.SnipSerializer;
import org.snipsnap.snip.storage.UserSerializer;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * Helper class for importing serialized database backups.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class XMLSnipImport {
  public final static int IMPORT_USERS = 0x01;
  public final static int IMPORT_SNIPS = 0x02;
  public final static int OVERWRITE = 0x04;

  /**
   * Load snips and users into the SnipSpace from an xml document out of a stream.
   * @param in  the input stream to load from
   * @param flags whether or not to overwrite existing content
   */
  public static void load(InputStream in, int flags) throws IOException {
    SAXReader saxReader = new SAXReader();
    try {
      Document document = saxReader.read(in);
      load(document, flags);
    } catch (DocumentException e) {
      Logger.warn("XMLSnipImport: unable to parse document", e);
      throw new IOException("Error parsing document: " + e);
    }
  }

  /**
   * Load snips and users into the SnipSpace from an xml document.
   * @param document the document to load from
   * @param flags whether or not to overwrite existing content
   */
  public static void load(Document document, int flags) {
    Element root = document.getRootElement();

    if ((flags & IMPORT_USERS) != 0) {
      UserSerializer userSerializer = UserSerializer.getInstance();
      UserManager userManager = (UserManager)Components.getComponent(UserManager.class);
      Iterator userIt = root.elementIterator("user");
      while (userIt.hasNext()) {
        Element userElement = (Element) userIt.next();
        Map userMap = userSerializer.getElementMap(userElement);
        userMap.remove(UserSerializer.USER_APPLICATION);

        String login = (String)userMap.get(UserSerializer.USER_NAME);
        String passwd = (String) userMap.get(UserSerializer.USER_PASSWORD);
        String email = (String) userMap.get(UserSerializer.USER_EMAIL);

        User user = null;
        if(userManager.exists(login)) {
          if ((flags & OVERWRITE) == 0) {
            Logger.log("ignoring to import user '"+login+"'");
            continue;
          }
          Logger.log("loading existing user '" + login + "'");
          user = userManager.load(login);
        } else {
          Logger.log("creating user '"+login+"'");
          user = userManager.create(login, passwd, email);
        }

        user = userSerializer.deserialize(userMap, user);
        userManager.systemStore(user);
      }
    }

    if ((flags & IMPORT_SNIPS) != 0) {
      SnipSerializer snipSerializer = SnipSerializer.getInstance();
      SnipSpace space = (SnipSpace)Components.getComponent(SnipSpace.class);

      User importUser = Application.get().getUser();
      UserManager um = (UserManager) Components.getComponent(UserManager.class);

      Iterator snipIt = root.elementIterator("snip");
      while (snipIt.hasNext()) {
        Element snipElement = (Element) snipIt.next();
        Map snipMap = snipSerializer.getElementMap(snipElement);
        snipMap.remove(SnipSerializer.SNIP_APPLICATION);

        String name = (String) snipMap.get(SnipSerializer.SNIP_NAME);
        String content = (String) snipMap.get(SnipSerializer.SNIP_CONTENT);
        Snip snip = null;
        if (space.exists(name)) {
          Logger.log("loading existing snip '" + name + "'");
          snip = space.load(name);
          if ((flags & OVERWRITE) == 0) {
            snip.setContent(snip.getContent() + content);
            snipMap.remove(SnipSerializer.SNIP_CONTENT);
          }
        } else {
          Logger.log("creating snip '" + name + "'");
          snip = space.create(name, content);
        }

        // check existing users
        if(!um.exists((String)snipMap.get(SnipSerializer.SNIP_CUSER))) {
          snipMap.put(SnipSerializer.SNIP_CUSER, importUser.getLogin());
        }
        if (!um.exists((String) snipMap.get(SnipSerializer.SNIP_MUSER))) {
          snipMap.put(SnipSerializer.SNIP_MUSER, importUser.getLogin());
        }
        if (!um.exists((String) snipMap.get(SnipSerializer.SNIP_OUSER))) {
          snipMap.put(SnipSerializer.SNIP_OUSER, importUser.getLogin());
        }

        snip = snipSerializer.deserialize(snipMap, snip);
        restoreAttachments(snipElement);
        snip.getBackLinks().getSize();
        space.systemStore(snip);
      }
    }
  }

  private static void restoreAttachments(Element snipEl) {
    Configuration config = Application.get().getConfiguration();
    File attRoot = config.getFilePath();
    Element attachmentsEl = snipEl.element("attachments");
    if(null != attachmentsEl) {
      Iterator attIt = attachmentsEl.elements("attachment").iterator();
      while (attIt.hasNext()) {
        Element att = (Element) attIt.next();
        if(att.element("data") != null) {
          File attFile = new File(attRoot, att.elementText("location"));
          try {
            // make sure the directory hierarchy exists
            attFile.getParentFile().mkdirs();
            byte buffer[] = Base64.decode(att.elementText("data").getBytes("UTF-8"));
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(attFile));
            os.write(buffer);
            os.flush();
            os.close();
          } catch (Exception e) {
            Logger.fatal("unable to store attachment: "+e);
            e.printStackTrace();
          }
        }
      }
    }
  }
}
