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

import org.snipsnap.render.filter.context.FilterContext;
import org.snipsnap.render.filter.FilterPipe;

/**
 * Acess point to dock several different rendering engines into
 * SnipSnap.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class RenderEngine {
  private FilterPipe fp;
  private static RenderEngine instance;

  public static synchronized RenderEngine getInstance() {
    if (null == instance) {
      instance = new RenderEngine();
    }
    return instance;
  }

  private RenderEngine() {
    fp = new FilterPipe();
    fp.addFilter("org.snipsnap.render.filter.EscapeFilter");
    fp.addFilter("org.snipsnap.render.filter.ParamFilter");
    fp.addFilter("org.snipsnap.render.filter.MacroFilter");
    fp.addFilter("org.snipsnap.render.filter.MacroFilter");
    fp.addFilter("org.snipsnap.render.filter.CalendarFilter");
    fp.addFilter("org.snipsnap.render.filter.HeadingFilter");
    fp.addFilter("org.snipsnap.render.filter.StrikeThroughFilter");
    fp.addFilter("org.snipsnap.render.filter.ListFilter");
    fp.addFilter("org.snipsnap.render.filter.NewlineFilter");
    fp.addFilter("org.snipsnap.render.filter.ParagraphFilter");
    fp.addFilter("org.snipsnap.render.filter.LineFilter");
    fp.addFilter("org.snipsnap.render.filter.BoldFilter");
    fp.addFilter("org.snipsnap.render.filter.ItalicFilter");
    fp.addFilter("org.snipsnap.render.filter.UrlFilter");
    fp.addFilter("org.snipsnap.render.filter.LinkTestFilter");
    fp.addFilter("org.snipsnap.render.filter.MarkFilter");
    fp.addFilter("org.snipsnap.render.filter.KeyFilter");
    fp.addFilter("org.snipsnap.render.filter.LateMacroFilter");
    fp.addFilter("org.snipsnap.render.filter.TypographyFilter");
  }

  public String render(String content, FilterContext context) {
    return fp.filter(content, context);
  }


}
