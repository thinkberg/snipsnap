/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
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
package org.snipsnap.snip.storage;

import org.snipsnap.snip.Links;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.Timestamp;

/**
 * A snip serializer that can store and load snips in XML format.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipSerializer {
  private final static String SNIP_NAME = "name";
  private final static String SNIP_CONTENT = "content";
  private final static String SNIP_OUSER = "oUser";
  private final static String SNIP_CUSER = "cUser";
  private final static String SNIP_MUSER = "mUser";
  private final static String SNIP_CTIME = "cTime";
  private final static String SNIP_MTIME = "mTime";
  private final static String SNIP_PARENT = "parentSnip";
  private final static String SNIP_PERMISSIONS = "permissions";
  private final static String SNIP_BACKLINKS = "backLinks";
  private final static String SNIP_SNIPLINKS = "snipLinks";
  private final static String SNIP_LABELS = "labels";
  private final static String SNIP_ATTACHMENTS = "attachments";
  private final static String SNIP_VIEWCOUNT = "viewCount";
  // TODO deprecated
  private final static String SNIP_COMMENTED = "commentSnip";

  private static SnipSerializer serializer = null;

  /**
   * Get an instance of the snip serializer.
   * @return the serializer
   */
  public static SnipSerializer getInstance() {
    if(null == serializer) {
      serializer = new SnipSerializer();
    }
    return serializer;
  }

  protected SnipSerializer() {
  }

  /**
   * Store a snip in an XML node.
   * @param parent parent document for creating the node
   * @param snip the snip to store
   * @return the serialized snip as XML
   */
  public Node serialize(Document parent, Snip snip) {
    //System.out.println("serialize("+snip.getName()+")");
    Node snipNode = parent.createElement("snip");
    snipNode.appendChild(createNode(parent, SNIP_NAME, snip.getName()));
    snipNode.appendChild(createNode(parent, SNIP_OUSER, snip.getOUser()));
    snipNode.appendChild(createNode(parent, SNIP_CUSER, snip.getCUser()));
    snipNode.appendChild(createNode(parent, SNIP_CTIME, ""+snip.getCTime().getTime()));
    snipNode.appendChild(createNode(parent, SNIP_MTIME, ""+snip.getMTime().getTime()));
    snipNode.appendChild(createNode(parent, SNIP_MUSER, "" + snip.getMUser()));
    Snip parentSnip = snip.getParent();
    snipNode.appendChild(createNode(parent, SNIP_PARENT, parentSnip != null ? parentSnip.getName() : ""));
    snipNode.appendChild(createNode(parent, SNIP_PERMISSIONS, snip.getPermissions().toString()));
    snipNode.appendChild(createNode(parent, SNIP_BACKLINKS, snip.getBackLinks().toString()));
    snipNode.appendChild(createNode(parent, SNIP_SNIPLINKS, snip.getSnipLinks().toString()));
    snipNode.appendChild(createNode(parent, SNIP_LABELS, snip.getLabels().toString()));
    snipNode.appendChild(createNode(parent, SNIP_ATTACHMENTS, snip.getAttachments().toString()));
    snipNode.appendChild(createNode(parent, SNIP_VIEWCOUNT, ""+snip.getViewCount()));
    Snip commentedSnip = snip.getCommentedSnip();
    snipNode.appendChild(createNode(parent, SNIP_COMMENTED, commentedSnip != null ? commentedSnip.getName() : ""));
    snipNode.appendChild(createNode(parent, SNIP_CONTENT, snip.getContent()));
    return snipNode;
  }

  /**
   * Create a node with the element name and value as specified.
   * @param parent parent document for creating the node
   * @param element the element name
   * @param value a value of the node (as string)
   * @return the new node
   */
  private Node createNode(Document parent, String element, String value) {
    if(value == null) {
      value = "";
    }
    Node node = parent.createElement(element);
    node.appendChild(parent.createTextNode(value));
    return node;
  }

  /**
   * Prepared for special storage of contents if it is not text but XML
   * @param parent parent document for creating the node
   * @param element the element name
   * @param value the xml content of the node
   * @return the new node
   */
  private Node createNode(Document parent, String element, Node value) {
    Node node = parent.createElement(element);
    if(value != null) {
      node.appendChild(value);
    }
    return node;
  }

  /**
   * Load snip from XML serialized file.
   * @param snipNode the XML node containing the snip
   * @param snip a snip to store the data in.
   * @return the modified snip
   */
  public Snip deserialize(Node snipNode, Snip snip) {
    NodeList nodeList = snipNode.getChildNodes();
    for(int item = 0; item < nodeList.getLength(); item++) {
      Node node = nodeList.item(item);

      if(node.getNodeType() == Node.ELEMENT_NODE) {
        String element = node.getNodeName();
        Node nodeContent = node.getFirstChild();
        String value = nodeContent != null ? nodeContent.getNodeValue() : "";
        if(SNIP_NAME.equals(element)) {
          //System.out.println("deserialize(" + value + ")");
          snip.setName(value);
        } else if(SNIP_CTIME.equals(element)) {
          snip.setCTime(new Timestamp(Long.parseLong(value)));
        } else if(SNIP_MTIME.equals(element)) {
          snip.setMTime(new Timestamp(Long.parseLong(value)));
        } else if(SNIP_CUSER.equals(element)) {
          snip.setCUser(value);
        } else if(SNIP_MUSER.equals(element)) {
          snip.setMUser(value);
        } else if (SNIP_OUSER.equals(element)) {
          snip.setOUser(value);
        } else if(SNIP_PARENT.equals(element)) {
          if (value.trim().length() != 0) {
            snip.setParentName(value);
          }
        } else if(SNIP_COMMENTED.equals(element)) {
          if (value.trim().length() != 0) {
            snip.setCommentedName(value);
          }
        } else if(SNIP_PERMISSIONS.equals(element)) {
          snip.setPermissions(new Permissions(value));
        } else if(SNIP_BACKLINKS.equals(element)) {
          snip.setBackLinks(new Links(value));
        } else if(SNIP_SNIPLINKS.equals(element)) {
          snip.setSnipLinks(new Links(value));
        } else if(SNIP_LABELS.equals(element)) {
          snip.setLabels(new Labels(value));
        } else if(SNIP_ATTACHMENTS.equals(element)) {
          snip.setAttachments(new Attachments(value));
        } else if(SNIP_VIEWCOUNT.equals(element)) {
          snip.setViewCount(Integer.parseInt(value));
        } else if(SNIP_CONTENT.equals(element)) {
          snip.setContent(value);
        }
      }
    }
    return snip;
  }
}
