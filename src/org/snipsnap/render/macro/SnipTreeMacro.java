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


package org.snipsnap.render.macro;

import org.radeox.macro.Macro;
import org.radeox.macro.BaseMacro;
import org.radeox.macro.parameter.MacroParameter;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/*
 * Macro for fulltext searches in SnipSnap. The macro
 * displays the search results for the input string. Can be
 * used in snips to "store" searches. For user defined
 * searches use a {field} macro combined with the {search}
 * macro.
 *
 * @author stephan
 * @version $Id$
 */

public class SnipTreeMacro extends BaseMacro {
  private SnipSpace space;

  private String[] paramDescription =
      {"1: Namespace prefix"};

  public SnipTreeMacro() {
    space = SnipSpaceFactory.getInstance();
  }

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "snip-tree";
  }

  public String getDescription() {
    return "Show a tree of snips from the namespace.";
  }

  private class Node {
    private String name;
    private boolean isSnip;
    private Map children;
    private String snipName;

    public Node(String name, boolean isSnip) {
      this.name = name;
      this.isSnip = isSnip;
      this.children = new LinkedHashMap();
    }

    public void setSnipName(String snipName) {
      this.snipName = snipName;
    }

    public String getSnipName() {
      return snipName;
    }

    public boolean hasChild(String name) {
      return children.containsKey(name);
    }

    public void addChild(Node node) {
      children.put(node.getName(), node);
    };

    public Node getChild(String name) {
      return (Node) children.get(name);
    }

    public boolean hasChildren() {
      return children.size() > 0;
    }

    public Collection getChildren() {
      return children.values();
    }

    public String getName() {
      return name;
    }

    public boolean isSnip() {
      return isSnip;
    }

    public String toString() {
      return name + " " + children.values().toString();
    }
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    // Names from the namespace look like
    // [0] = foo/
    // [1] = foo/bar
    // [2] = foo/barbar
    // [3] = foo/barbar/boing

    if (params.getLength() == 1) {
      Snip[] snips = space.match(params.get("0"));

      Node root = new Node("root", false);

      for (int i = 0; i < snips.length; i++) {
        Snip snip = snips[i];
        String elements[] = snip.getName().split("/");

        // Create all nodes till leaf
        Node lastNode = root;
        for (int j = 0; j < elements.length; j++) {
          String name = elements[j];
          if (!lastNode.hasChild(name)) {
            boolean isSnip = (j == elements.length - 1);
            Node node = new Node(name, isSnip);
            if (isSnip) {
              node.setSnipName(snip.getName());
            }
            lastNode.addChild(node);
            lastNode = node;
          } else {
            lastNode = lastNode.getChild(name);
          }
        }
      }

      writer.write("<div class=\"snip-tree\">");
      writeTree(writer, root);
      writer.write("</div>");
    } else if (params.getLength() == 2) {
      writer.write("<img src=\"/exec/namespace?name=" + params.get(0) + "\"/>");
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }

  public void writeTree(Writer writer, Node node) throws IOException {
    Iterator children = node.getChildren().iterator();
    writer.write("<ul>");
    while (children.hasNext()) {
      Node child = (Node) children.next();
      writer.write("<li>");
      if (child.isSnip()) {
        SnipLink.appendLink(writer, child.getSnipName(), child.getName());
      } else {
        writer.write(child.getName());
      }
      writer.write("</li>");
      if (child.hasChildren()) {
        writeTree(writer, child);
      }
    }
    writer.write("</ul>");
  }
}
