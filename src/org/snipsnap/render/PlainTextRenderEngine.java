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

package org.snipsnap.render;

import org.radeox.engine.RenderEngine;
import org.radeox.engine.context.BaseInitialRenderContext;
import org.radeox.engine.context.InitialRenderContext;
import org.radeox.engine.context.RenderContext;
import org.radeox.filter.EscapeFilter;
import org.radeox.filter.FilterPipe;
import org.radeox.filter.UrlFilter;
import org.radeox.filter.context.BaseFilterContext;
import org.radeox.filter.context.FilterContext;

import java.io.IOException;
import java.io.Writer;

/**
 * Renderengine for preformattet text.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class PlainTextRenderEngine implements RenderEngine {
  private InitialRenderContext initialContext;
  protected FilterPipe fp;

  public PlainTextRenderEngine() {
    initialContext = new BaseInitialRenderContext();
  }

  protected void init() {
    if (null == fp) {
      fp = new FilterPipe(initialContext);
      fp.addFilter(new EscapeFilter());
      fp.addFilter(new UrlFilter());
      fp.init();
    }
  }

  public String getName() {
    return "PlainText";
  }

  public String render(String content, RenderContext context) {
    init();
    FilterContext filterContext = new BaseFilterContext();
    filterContext.setRenderContext(context);
    StringBuffer plainText = new StringBuffer("<div id=\"code\"><pre>");
    plainText.append(fp.filter(content, filterContext));
    plainText.append("</pre></div>").toString();
    return plainText.toString();
  }

  public void render(Writer out, String content, RenderContext context) throws IOException {
    out.write(render(content, context));
  }

}
