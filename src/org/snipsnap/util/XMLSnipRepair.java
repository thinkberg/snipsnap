/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * CopyAtright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Repair XML File
 */
public class XMLSnipRepair {
  public static void main(String args[]) {
    try {
      load(new FileInputStream(args[0]), new FileOutputStream(args[1]));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static DocumentBuilder documentBuilder;

  /**
   * Load snips and users into the SnipSpace from an xml document out of a stream.
   * @param in  the input stream to load from
   */
  private static void load(InputStream in, OutputStream out) throws IOException {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (FactoryConfigurationError error) {
      System.err.println("Unable to create document builder factory: " + error);
    } catch (ParserConfigurationException e) {
      System.err.println("Unable to create document builder");
      e.printStackTrace();
    }

    try {
      System.out.print("Parsing input document ...");
      Document document = documentBuilder.parse(in);
      System.out.println(" done.");
      Document repaired = repair(document);
      StreamResult streamResult = new StreamResult(out);
      TransformerFactory tf = SAXTransformerFactory.newInstance();
      Transformer serializer = tf.newTransformer();
      serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      System.out.print("Writing output document ...");
      serializer.transform(new DOMSource(repaired), streamResult);
      System.out.println(" done.");
    } catch (Exception e) {
      System.err.println("XMLSnipImport: error: " + e.getMessage());
    }
  }

  private static Document repair(Document document) {
    Map data = new TreeMap();
    Node rootNode = document.getFirstChild();
    System.out.println("root: " + rootNode.getNodeName());
    NodeList children = rootNode.getChildNodes();

    long identDup = 0;
    long oldDup = 0;
    long newDup = 0;
    while (children.getLength() > 0) {
      Node node = children.item(0);
      Node idNode = null;
      if ("user".equals(node.getNodeName())) {
        idNode = getChildNode(node, "login");
      } else if ("snip".equals(node.getNodeName())) {
        idNode = getChildNode(node, "name");
      }

      if (null != idNode) {
        String id = node.getNodeName() + "[" + idNode.getFirstChild().getNodeValue() + "]";
        Node changed = getChildNode(node, "mTime");
        long mtime = Long.parseLong(changed.getFirstChild().getNodeValue());
        Node lastNode = (Node) data.get(id);
        if (lastNode != null) {
          Node lastChanged = getChildNode(lastNode, "mTime");
          long lastmtime = Long.parseLong(lastChanged.getFirstChild().getNodeValue());
          if (mtime > lastmtime) {
            newDup++;
            System.out.println("Replacing duplicate by newer node: " + id + " (" + (mtime - lastmtime) + "ms)");
            data.put(id, node);
          } else if (mtime == lastmtime) {
            identDup++;
            System.out.println("Identical duplicate found: " + id);
          } else {
            oldDup++;
            System.out.println("Older duplicate found: " + id);
          }
        } else {
          data.put(id, node);
        }
      } else if (Node.TEXT_NODE != node.getNodeType()) {
        System.out.println("Unknown node '" + node.getNodeName() + "', ignoring ...");
        data.put(node, node);
      }
      rootNode.removeChild(node);
    }
    Iterator it = data.values().iterator();
    while (it.hasNext()) {
      rootNode.appendChild((Node) it.next());
    }
    System.out.println("Found "+identDup+" identical duplicates, replaced "+newDup+", ignored "+oldDup+".");
    System.out.println("Repair done.");
    return document;
  }

  private static Node getChildNode(Node parent, String name) {
    NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (name.equals(child.getNodeName())) {
        return child;
      }
    }
    return null;
  }

}
