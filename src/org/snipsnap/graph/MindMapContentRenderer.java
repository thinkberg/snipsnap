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

import org.snipsnap.graph.builder.StringTreeBuilder;
import org.snipsnap.graph.builder.TreeBuilder;
import org.snipsnap.graph.context.GraphRendererContext;
import org.snipsnap.graph.renderer.MindMapRenderer;
import org.snipsnap.graph.renderer.Renderer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MindMapContentRenderer implements ContentRenderer {
  private Renderer renderer = new MindMapRenderer();

  public String getName() {
    return "mindmap";
  }

  public Renderer getRenderer() {
    return renderer;
  }

  public void render(HttpServletRequest request, HttpServletResponse response, String content) throws IOException {
    response.setContentType("image/png");

    ServletOutputStream out = response.getOutputStream();

    TreeBuilder builder = new StringTreeBuilder(content);
    renderer.render(builder.build(), out, new GraphRendererContext());
  }
}
