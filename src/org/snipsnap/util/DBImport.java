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

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.snipsnap.user.User;
import org.snipsnap.user.Roles;
import org.snipsnap.user.UserManager;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.config.Configuration;
import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class DBImport {
  public static void main(String args[]) {
    if(args.length < 3) {
      System.out.println("usage: DBImport <application> <user> <xml-file>");
      System.exit(-1);
    }

    Configuration serverConfig = null;
    try {
      serverConfig = new Configuration("./conf/server.conf");
    } catch (IOException e) {
      System.out.println("Unable to load server config: "+e);
      System.exit(-1);
    }
    Application app = Application.get();
    AppConfiguration config = null;
    try {
      config = new AppConfiguration(
           new File(serverConfig.getProperty(Configuration.SERVER_WEBAPP_ROOT)+args[0]+"/application.conf"));
    } catch (IOException e) {
      System.out.println("Unable to load application config: "+e);
      System.exit(-1);
    }
    app.setConfiguration(config);

    UserManager um = UserManager.getInstance();
    User user = um.load(args[1]);
    app.setUser(user);

    if(user == null) {
      System.err.println("User not found: "+args[1]);
      System.exit(-1);
    }

    File in = new File(args[2]);

    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

      Document document = documentBuilder.parse(in);
      NodeList children = document.getChildNodes().item(0).getChildNodes();
      for(int i = 0; i < children.getLength(); i++) {
        Node node = children.item(i);
	if("user".equals(node.getNodeName())) {
	  insertUser(node);
	}
      }

      for(int i = 0; i < children.getLength(); i++) {
        Node node = children.item(i);
        if("snip".equals(node.getNodeName())) {
          insertSnip(node);
        } 
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error while reading import file: "+e);
    }
    System.exit(0);
  }
  private static void insertUser(Node snipNode) throws SAXException {
    UserManager um = UserManager.getInstance();
    NodeList children = snipNode.getChildNodes();

    Map elements = new HashMap();

    for(int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String name = node.getNodeName();
      String value = null;
      NodeList cl = node.getChildNodes();
      for(int c = 0; c < cl.getLength(); c++) {
        if(cl.item(c).getNodeType() == Node.TEXT_NODE) {
          value = cl.item(c).getNodeValue();
        }
      }
      elements.put(name, value);
    }

    String login = (String)elements.get("login");
    String passwd = (String)elements.get("passwd");
    String email = (String)elements.get("email");
    String status = (String)elements.get("status");
    String roles = (String)elements.get("roles");

    User user = um.load(login);
    if(user == null) {
      System.err.println("creating user '"+login+"'");
      user = um.create(login, passwd, email);
      if(status != null) user.setStatus(status);
      if(roles != null) user.setRoles(new Roles(roles));
    } else {
      System.err.println("modifying user '"+login+"'");
      if(passwd != null) user.setPasswd(passwd);
      if(email != null) user.setEmail(email);
      if(status != null) user.setStatus(status);
      if(roles != null) user.setRoles(new Roles(roles));
    }
    um.store(user);
  }

  private static void insertSnip(Node snipNode) throws SAXException {
    SnipSpace space = SnipSpace.getInstance();
    UserManager um = UserManager.getInstance();
    NodeList children = snipNode.getChildNodes();

    Map elements = new HashMap();

    for(int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String name = node.getNodeName();
      String value = null;
      NodeList cl = node.getChildNodes();
      for(int c = 0; c < cl.getLength(); c++) {
        if(cl.item(c).getNodeType() == Node.TEXT_NODE) {
          value = cl.item(c).getNodeValue();
        }
      }
      elements.put(name, value);
    }

    String name = (String)elements.get("name");
    if(name != null) {
      // load content
      Snip snip = null;
      if(space.exists(name)) {
        System.out.println("appending to '"+name+"'");
        snip = space.load(name);
      } else {
        System.out.println("creating node '"+name+"'");
        snip = space.create(name, "");
      }
      // add snip info and content
      String tmp;
      if((tmp = (String)elements.get("content")) != null) {
	String content = snip.getContent();
	if(content != null && content.length() > 0) {
	  content += "\n"+tmp;
	} else content = tmp;
        snip.setContent(content);
      }
      if((tmp = (String)elements.get("cTime")) != null) {
	snip.setCTime(getTimestamp(tmp));
      }
      if((tmp = (String)elements.get("mTime")) != null) {
	snip.setMTime(getTimestamp(tmp));
      }
      if((tmp = (String)elements.get("cUser")) != null) {
	User user = um.load(tmp);
	if(user != null) {
	  snip.setCUser(user);
	}
      }
      if((tmp = (String)elements.get("mUser")) != null) {
	User user = um.load(tmp);
	if(user != null) {
	  snip.setMUser(user);
	}
      }

      space.store(snip);
    }
  }

  private static Timestamp getTimestamp(String t) {
    return new Timestamp(new Date(Long.parseLong(t)).getTime());
  }
}
