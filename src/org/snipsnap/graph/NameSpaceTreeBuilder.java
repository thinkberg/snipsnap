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
package org.snipsnap.graph;

import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;

public class NameSpaceTreeBuilder implements TreeBuilder {
  private String root;

  public NameSpaceTreeBuilder(String root) {
    this.root = root;
  }

  public Tree build() {
    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);

    Snip[] snips = space.match(root);

    if (root.endsWith("/")) {
      root = root.substring(0, root.length()-1);
    }

    Node parent = new Node(root);
    Node lastNode = parent;
    Tree tree = new Tree(parent);

    int depth = -1;
    int currentDepth = -1;
    for (int i = 0; i < snips.length; i++) {
      Snip snip = snips[i];
      String elements[] = snip.getName().split("/");
      currentDepth = elements.length - 1;
      String element = elements[currentDepth];

      if (-1 == depth) {
        depth = currentDepth;
      }
      if (currentDepth > depth) {
        depth = currentDepth;
        Node child = new Node(element, lastNode);
        parent = lastNode;
        parent.addChild(child);
        lastNode = child;
      } else if (currentDepth < depth) {
        depth = currentDepth;
        parent = parent.getParent();
        Node child = new Node(element, parent);
        parent.addChild(child);
        lastNode = child;
      } else {
        Node child = new Node(element, parent);
        parent.addChild(child);
        lastNode = child;
      }
    }
    tree.setRowCounter(tree.getDepth());
    int maxChildren[] = new Maximum().getMaxChildren(tree);
    int maxAttributes[] = new Maximum().getMaxAttributes(tree);
    tree.setMaxChildren(maxChildren);
    tree.setMaxAttributes(maxAttributes);

    System.err.println("Tree="+tree);
    System.err.println("Tree depth="+tree.getDepth());
    return tree;
  }
}
