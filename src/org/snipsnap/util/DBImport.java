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
package org.snipsnap.util;

import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.config.Configuration;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.Links;
import org.snipsnap.snip.Labels;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.Permissions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class DBImport {

  public static Map missingParent = new HashMap();
  public static Map missingCommentSnip = new HashMap();

  public static void main(String args[]) {
    if (args.length < 3) {
      System.out.println("usage: DBImport <application> <user> <xml-file> [overwrite]");
      System.exit(-1);
    }

    Configuration serverConfig = null;
    try {
      serverConfig = new Configuration("./conf/server.conf");
    } catch (IOException e) {
      System.out.println("Unable to load server config: " + e);
      System.exit(-1);
    }

    Application app = Application.get();
    AppConfiguration config = null;
    try {
      config = new AppConfiguration(
        new File(serverConfig.getProperty(Configuration.SERVER_WEBAPP_ROOT) + args[0] + "/application.conf"));
    } catch (IOException e) {
      System.out.println("Unable to load application config: " + e);
      System.exit(-1);
    }
    app.setConfiguration(config);

    UserManager um = UserManager.getInstance();
    User user = um.load(args[1]);
    app.setUser(user);

    if (user == null) {
      System.err.println("User not found: " + args[1]);
      System.exit(-1);
    }

    boolean overwrite = args.length == 4 && "overwrite".equals(args[3]);

    File in = new File(args[2]);

    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

      Document document = documentBuilder.parse(in);
      NodeList children = document.getChildNodes().item(0).getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node node = children.item(i);
        if ("user".equals(node.getNodeName())) {
          insertUser(node);
        }
      }

      for (int i = 0; i < children.getLength(); i++) {
        Node node = children.item(i);
        if ("snip".equals(node.getNodeName())) {
          insertSnip(node, overwrite);
        }
      }

      SnipSpace space = SnipSpace.getInstance();
      if(!missingParent.isEmpty()) {
        System.out.println("Inserting previously missing parents ...");
        Iterator it = missingParent.keySet().iterator();
        while(it.hasNext()) {
          Snip snip = (Snip)it.next();
          String parentName = (String) missingParent.get(snip);
          if(space.exists(parentName)) {
            snip.setParent(space.load(parentName));
          } else {
            System.out.println("parent '"+parentName+"' for snip '"+snip.getName()+"' missing");
          }
        }
      }

      if(!missingCommentSnip.isEmpty()) {
        System.out.println("Inserting previously missing commented snips ...");
        Iterator it = missingParent.keySet().iterator();
        while(it.hasNext()) {
          Snip snip = (Snip)it.next();
          String parentName = (String) missingParent.get(snip);
          if(space.exists(parentName)) {
            snip.setCommentedSnip(space.load(parentName));
          } else {
            System.out.println("snip '"+parentName+"' for comment '"+snip.getName()+"' missing");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error while reading import file: " + e);
    }
    System.exit(0);
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
      System.err.println("creating user '" + login + "'");
      user = um.create(login, passwd, email);
    } else {
      if (passwd != null) user.setPasswd(passwd);
      if (email != null) user.setEmail(email);
    }

    System.err.println("modifying user properties for '" + login + "'");
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
      <labels></labels>
      <viewCount>5</viewCount>
      <permissions></permissions>
    </snip>
   */
  private static void insertSnip(Node snipNode, boolean overwrite) throws SAXException {
    SnipSpace space = SnipSpace.getInstance();
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

      if ((tmp = (String) elements.get("cUser")) != null) {
        User user = um.load(tmp);
        if (user != null) {
          snip.setCUser(user);
        }
      }

      // store last modification time
      if ((tmp = (String) elements.get("mTime")) != null) {
        snip.setMTime(getTimestamp(tmp));
      } else {
        snip.setMTime(new Timestamp(new java.util.Date().getTime()));
      }

      // store modification user
      User mUser = null;
      if ((tmp = (String) elements.get("mUser")) != null) {
        mUser = um.load(tmp);
      }
      if (null == mUser) {
        mUser = Application.get().getUser();
      }
      snip.setMUser(tmp);

      if ((tmp = (String) elements.get("parentSnip")) != null) {
        if(space.exists(tmp)) {
          snip.setParent(space.load(tmp));
        } else {
          missingParent.put(snip, tmp);
        }
      }

      if ((tmp = (String) elements.get("commentSnip")) != null) {
        if(space.exists(tmp)) {
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

      if ((tmp = (String) elements.get("viewCount")) != null) {
        try {
          snip.setViewCount(Integer.parseInt(tmp));
        } catch (NumberFormatException e) {
          System.err.println("error: viewcount '"+tmp+"' for snip '"+name+"' is not a number");
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
