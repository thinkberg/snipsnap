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

import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Import
 */
public class XMLSnipImport {

  private static Map missingParent = new HashMap();
  private static Map missingCommentSnip = new HashMap();

  /**
   * Load snips and users into the SnipSpace from an xml document out of a stream.
   * @param in  the input stream to load from
   * @param flags whether or not to overwrite existing content
   */
  public static void load(InputStream in, int flags) throws IOException {
    DocumentBuilder documentBuilder = null;
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (FactoryConfigurationError error) {
      Logger.warn("Unable to create document builder factory: " + error);
    } catch (ParserConfigurationException e) {
      Logger.warn("Unable to create document builder", e);
    }

    Document document = null;
    try {
      document = documentBuilder.parse(in);
      load(document, flags);
    } catch (SAXException e) {
      Logger.warn("XMLSnipImport: unable to parse document", e);
      throw new IOException("Error parsing document: " + e);
    }
  }


  public final static int IMPORT_USERS = 0x01;
  public final static int IMPORT_SNIPS = 0x02;
  public final static int OVERWRITE = 0x04;

  /**
   * Load snips and users into the SnipSpace from an xml document.
   * @param document the document to load from
   * @param flags whether or not to overwrite existing content
   */
  public static void load(Document document, int flags) throws SAXException {
    NodeList children = document.getChildNodes().item(0).getChildNodes();

    if ((flags & IMPORT_USERS) != 0) {
      for (int i = 0; i < children.getLength(); i++) {
        Node node = children.item(i);
        if ("user".equals(node.getNodeName())) {
          insertUser(node);
        }
      }
    }

    if ((flags & IMPORT_SNIPS) != 0) {
      for (int i = 0; i < children.getLength(); i++) {
        Node node = children.item(i);
        if ("snip".equals(node.getNodeName())) {
          insertSnip(node, (flags & OVERWRITE) != 0);
        }
      }

      SnipSpace space = SnipSpaceFactory.getInstance();
      if (!missingParent.isEmpty()) {
        System.out.println("Inserting previously missing parents ...");
        Iterator it = missingParent.keySet().iterator();
        while (it.hasNext()) {
          Snip snip = (Snip) it.next();
          String parentName = (String) missingParent.get(snip);
          if (space.exists(parentName)) {
            System.out.println("setting parent of '" + snip.getName() + "' to '" + parentName + "'");
            snip.setParent(space.load(parentName));
            space.systemStore(snip);
          } else {
            System.out.println("parent '" + parentName + "' for snip '" + snip.getName() + "' missing");
          }
        }
      }

      if (!missingCommentSnip.isEmpty()) {
        System.out.println("Inserting previously missing commented snips ...");
        Iterator it = missingCommentSnip.keySet().iterator();
        while (it.hasNext()) {
          Snip snip = (Snip) it.next();
          String parentName = (String) missingCommentSnip.get(snip);
          System.out.println("snip: " + snip + ", " + parentName);
          if (space.exists(parentName)) {
            System.out.println("setting commented snip of '" + snip.getName() + "' to '" + parentName + "'");
            snip.setCommentedSnip(space.load(parentName));
            space.systemStore(snip);
          } else {
            System.out.println("snip '" + parentName + "' for comment '" + snip.getName() + "' missing");
          }
        }
      }
    }
  }

  /**
   * Create or modify user in the database.
   *
   *  <user>
   *    <login>TestDummy</login>
   *    <passwd>boing</passwd>
   *    <email></email>
   *    <status></status>
   *    <roles></roles>
   *    <cTime>1029430469000</cTime>
   *    <mTime>1029430469000</mTime>
   *    <lastLogin>1029431779000</lastLogin>
   *  </user>
   * @param snipNode the snip node the user is stored in
   */
  private static void insertUser(Node snipNode) throws SAXException {
    UserManager um = UserManager.getInstance();

    Map elements = getElements(snipNode);

    String login = (String) elements.get("login");
    String passwd = (String) elements.get("passwd");
    String email = (String) elements.get("email");
    String status = (String) elements.get("status");
    String roles = (String) elements.get("roles");
    String cTime = (String) elements.get("cTime");
    String mTime = (String) elements.get("mTime");
    String llogin = (String) elements.get("lastLogin");
    String lastAccess = (String) elements.get("lastAccess");
    String lastLogout = (String) elements.get("lastLogout");

    User user = um.load(login);
    if (user == null) {
      Logger.debug("creating user '" + login + "'");
      user = um.create(login, passwd, email);
    } else {
      if (passwd != null) user.setPasswd(passwd);
      if (email != null) user.setEmail(email);
    }

    Logger.debug("modifying user properties for '" + login + "'");
    if (status != null) user.setStatus(status);
    if (roles != null) user.setRoles(new Roles(roles));
    if (cTime != null) user.setCTime(getTimestamp(cTime));
    if (mTime != null) user.setMTime(getTimestamp(mTime));
    if (llogin != null) user.setLastLogin(getTimestamp(llogin));
    if (lastAccess != null) user.setLastAccess(getTimestamp(lastAccess));
    if (lastLogout != null) user.setLastLogout(getTimestamp(lastLogout));
    um.store(user);
  }


  /**
   * Store or modify snip.
   * <snip>
   *   <name>Reflection Performance</name>
   *   <content>[Reflection] in Java ist generell langsamer als andere Methoden.
   *     [french_c].~~
   *   </content>
   *   <cTime>1029428690000</cTime>
   *   <mTime>1029428691000</mTime>
   *   <cUser>funzel</cUser>
   *   <mUser>funzel</mUser>
   *   <backLinks></backLinks>
   *   <snipLinks>Reflection:1|snipsnap-search:1|Java Performance:1</snipLinks>
   *   <labels></labels>
   *   <attachments></attachments>
   *   <viewCount>5</viewCount>
   *   <permissions></permissions>
   * </snip>
   */
  private static void insertSnip(Node snipNode, boolean overwrite) throws SAXException {
    SnipSpace space = SnipSpaceFactory.getInstance();
    UserManager um = UserManager.getInstance();

    Map elements = getElements(snipNode);

    String name = (String) elements.get("name");
    if (name != null) {
      // load content
      Snip snip = null;
      if (space.exists(name)) {
        snip = space.load(name);
      } else {
        snip = space.create(name, "");
      }

      // add snip info and content
      String tmp;
      if ((tmp = (String) elements.get("content")) != null) {
        String content = snip.getContent();
        if (content != null && content.length() > 0 && !overwrite) {
          System.out.println("appending to '" + name + "'");
          content += "\n" + tmp;
        } else {
          System.out.println("creating node '" + name + "'");
          content = tmp;
        }
        snip.setContent(content);
      }

      if ((tmp = (String) elements.get("cTime")) != null) {
        snip.setCTime(getTimestamp(tmp));
      }

      // creation user
      User cUser = null;
      if ((tmp = (String) elements.get("cUser")) != null && um.load(tmp) != null) {
        snip.setCUser(um.load(tmp));
      } else if (snip.getCUser() == null) {
        snip.setCUser(Application.get().getUser());
      }

      // owner user
      User oUser = null;
      if ((tmp = (String) elements.get("oUser")) != null && um.load(tmp) != null) {
        snip.setOUser(um.load(tmp));
      } else if (snip.getOUser() == null) {
        snip.setOUser(Application.get().getUser());
      }

      // store modification user
      User mUser = null;
      if ((tmp = (String) elements.get("mUser")) != null && um.load(tmp) != null) {
        snip.setMUser(um.load(tmp));
      } else if (snip.getMUser() == null) {
        snip.setMUser(Application.get().getUser());
      }

      // store last modification time
      if ((tmp = (String) elements.get("mTime")) != null) {
        snip.setMTime(getTimestamp(tmp));
      } else {
        snip.setMTime(new Timestamp(new java.util.Date().getTime()));
      }

      if ((tmp = (String) elements.get("parentSnip")) != null) {
        if (space.exists(tmp)) {
          snip.setParent(space.load(tmp));
        } else {
          missingParent.put(snip, tmp);
        }
      }

      if ((tmp = (String) elements.get("commentSnip")) != null) {
        System.out.println("'" + name + "' is a comment to '" + tmp + "' exists? " + space.exists(tmp));
        if (space.exists(tmp)) {
          snip.setCommentedSnip(space.load(tmp));
        } else {
          missingCommentSnip.put(snip, tmp);
        }
      }

      if ((tmp = (String) elements.get("backLinks")) != null) {
        snip.setBackLinks(new Links(tmp));
      }

      if ((tmp = (String) elements.get("snipLinks")) != null) {
        snip.setSnipLinks(new Links(tmp));
      }

      if ((tmp = (String) elements.get("labels")) != null) {
        snip.setLabels(new Labels(tmp));
      }

      if((tmp = (String) elements.get("attachments")) != null) {
        snip.setAttachments(new Attachments(tmp));
      }

      if ((tmp = (String) elements.get("viewCount")) != null) {
        try {
          snip.setViewCount(Integer.parseInt(tmp));
        } catch (NumberFormatException e) {
          Logger.warn("error: viewcount '" + tmp + "' for snip '" + name + "' is not a number");
        }
      }

      if ((tmp = (String) elements.get("permissions")) != null) {
        snip.setPermissions(new Permissions(tmp));
      }

      // store directly
      space.systemStore(snip);
    }
  }

  private static Timestamp getTimestamp(String t) {
    return new Timestamp(new Date(Long.parseLong(t)).getTime());
  }

  private static Map getElements(Node snipNode) {
    NodeList children = snipNode.getChildNodes();

    Map elements = new HashMap();

    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String name = node.getNodeName();
      String value = null;
      NodeList cl = node.getChildNodes();
      for (int c = 0; c < cl.getLength(); c++) {
        if (cl.item(c).getNodeType() == Node.TEXT_NODE) {
          value = cl.item(c).getNodeValue();
        }
      }
      elements.put(name, value);
    }
    return elements;
  }

}
