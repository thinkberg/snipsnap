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
        if(node.getNodeName().equals("snip")) {
          insertSnip(node);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error while reading import file: "+e);
    }
    System.exit(0);
  }

  private static void insertSnip(Node snipNode) throws SAXException {
    SnipSpace space = SnipSpace.getInstance();
    NodeList children = snipNode.getChildNodes();

    String name = null;
    String content = null;

    for(int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if(node.getNodeName().equals("name")) {
        NodeList cl = node.getChildNodes();
        for(int c = 0; c < cl.getLength(); c++) {
          if(cl.item(c).getNodeType() == Node.TEXT_NODE) {
            name = cl.item(c).getNodeValue();
          }
        }
      } else if(node.getNodeName().equals("content")) {
        NodeList cl = node.getChildNodes();
        for(int c = 0; c < cl.getLength(); c++) {
          if(cl.item(c).getNodeType() == Node.TEXT_NODE) {
            content = cl.item(c).getNodeValue();
          }
        }
      }
    }

    if(name != null && content != null) {
      Snip snip = null;
      if(space.exists(name)) {
        System.out.println("appending to '"+name+"'");
        snip = space.load(name);
        snip.setContent(snip.getContent()+"\n"+content);
      } else {
        System.out.println("creating node '"+name+"'");
        snip = space.create(name, content);
      }
      space.store(snip);
    }
  }
}
