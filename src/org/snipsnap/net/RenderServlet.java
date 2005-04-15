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
package org.snipsnap.net;

import org.radeox.util.Service;
import org.snipsnap.graph.ContentRenderer;
import org.snipsnap.graph.HorizontalContentRenderer;
import org.snipsnap.graph.builder.StringTreeBuilder;
import org.snipsnap.graph.builder.TreeBuilder;
import org.snipsnap.graph.context.UrlContext;
import org.snipsnap.graph.renderer.HtmlMapRenderer;
import org.snipsnap.graph.renderer.Renderer;
import snipsnap.api.app.Application;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Render special content added to a temporary store. This is used for the
 * graph macro. The main use is to add content to the page that is retrieved
 * by img tags or similar.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class RenderServlet extends HttpServlet {
  private final static String RENDER_ID = "__render_id";

  private static Map contentMap = Collections.synchronizedMap(new HashMap());
  private static Map handlers = new HashMap();
  private final static ContentRenderer DEFAULT_HANDLER = new HorizontalContentRenderer();

  /**
   * Initialize the render servlet by loading the content handlers.
   */
  static {
    Iterator contentRenderer =
            Service.providers(org.snipsnap.graph.ContentRenderer.class);
    while (contentRenderer.hasNext()) {
      ContentRenderer renderer = (ContentRenderer) contentRenderer.next();
      handlers.put(renderer.getName(), renderer);
    }
  }


  /**
   * Add content to the temporary store and return an id that can be used to select
   * the content later. The graph macro uses this to store the graph description
   * here which is then handed over to the rendering handler to translate to an image.
   * The id will persist until content for the same name is added.
   * <p/>
   * Example:
   * &lt;img src="/exec/render?id=XXXX&handler=YYYY"/&gt;
   *
   * @param content the textual content to be rendered
   * @return an it to add to the url for retrieving the rendered content
   */
  public static String addContent(String name, String content) {
    Application app = Application.get();
    String baseId = RENDER_ID + name;
    String renderId = null;
    synchronized (contentMap) {
      String key = null;
      int add = 0;
      do {
        key = String.valueOf(baseId + add++);
      } while (app.getObject(key) != null);
      // store a dummy to ensure the id is taken
      app.storeObject(key, "");
      // store content with corresponding id
      renderId = Integer.toHexString(key.hashCode());
      contentMap.put(renderId, content);
    }
    return renderId;
  }

  public static String getImageMap(String renderId, String handler) {
    HtmlMapRenderer mapRenderer = new HtmlMapRenderer();
    TreeBuilder builder = new StringTreeBuilder((String) contentMap.get(renderId));
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    Renderer renderer = ((ContentRenderer) handlers.get(handler)).getRenderer();
    if (null != renderer) {
      UrlContext context = new UrlContext(renderId, renderer);
      mapRenderer.render(builder.build(), out, context);
      try {
        out.flush();
        out.close();
      } catch (IOException e) {
        // ignore as this is unlikely to happen
        e.printStackTrace();
      }
      try {
        return out.toString(Application.get().getConfiguration().getEncoding());
      } catch (UnsupportedEncodingException e) {
        return out.toString();
      }
    } else {
      // we can't render the image map, so return comment
      return "<!-- image map not possible, missing renderer for handler: " + handler + " -->";
    }
  }


  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    String handler = request.getParameter("handler");
    String id = request.getParameter("id");
    String content = (String) contentMap.get(id);

    ContentRenderer renderer = (ContentRenderer) handlers.get(handler);
    if (null == renderer) {
      renderer = DEFAULT_HANDLER;
    }
    renderer.render(request, response, content);
  }
}