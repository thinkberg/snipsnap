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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;

/**
 * Render special content added to a temporary store. This is used for the
 * graph macro. The main use is to add content to the page that is retrieved
 * by img tags or similar.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class RenderServlet extends HttpServlet {
  private static Map contentMap = Collections.synchronizedMap(new HashMap());
  private Map handlers = new HashMap();
  private final static ContentRenderer DEFAULT_HANDLER = new HorizontalContentRenderer();

  /**
   * Add content to the temporary store and return an id that can be used to select
   * the content later. The graph macro uses this to store the graph description
   * here which is then handed over to the rendering handler to translate to an image.
   * <p/>
   * Example:
   * &lt;img src="/exec/render?id=XXXX&handler=YYYY"/&gt;
   *
   * @param content the textual content to be rendered
   * @return an it to add to the url for retrieving the rendered content
   */
  public static String addContent(String content) {
    String key = null;
    synchronized (contentMap) {
      int add = 0;
      int hashCode = content.hashCode();
      do {
        key = String.valueOf(hashCode + add++);
      } while (contentMap.containsKey(key));
      contentMap.put(key, content);
    }
    return key;
  }

  /**
   * Initialize the render servlet by loading the content handlers.
   *
   * @throws ServletException
   */
  public void init() throws ServletException {
    Iterator contentRenderer =
            Service.providers(org.snipsnap.graph.ContentRenderer.class);
    while (contentRenderer.hasNext()) {
      ContentRenderer renderer = (ContentRenderer) contentRenderer.next();
      handlers.put(renderer.getName(), renderer);
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    String handler = request.getParameter("handler");
    String id = request.getParameter("id");
    String content = (String) contentMap.get(id);
    contentMap.remove(id);

    ContentRenderer renderer = (ContentRenderer) handlers.get(handler);
    if (null == renderer) {
      renderer = DEFAULT_HANDLER;
    }
    renderer.render(request, response, content);
  }
}