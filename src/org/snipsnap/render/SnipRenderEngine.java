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

import org.radeox.engine.BaseRenderEngine;
import org.radeox.api.engine.ImageRenderEngine;
import org.radeox.api.engine.IncludeRenderEngine;
import org.radeox.api.engine.WikiRenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.filter.context.FilterContext;
import org.snipsnap.app.Application;
import org.snipsnap.render.context.SnipRenderContext;
import org.snipsnap.render.filter.context.SnipFilterContext;
import org.snipsnap.serialization.StringBufferWriter;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.AuthenticationService;

import java.io.IOException;
import java.io.Writer;

/**
 * renderEngine implementation for SnipSnap which understoods e.g.
 * howto include other snips.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipRenderEngine extends BaseRenderEngine
    implements WikiRenderEngine, IncludeRenderEngine, ImageRenderEngine {

  private SnipSpace space;
  private AuthenticationService authService;

  public SnipRenderEngine(AuthenticationService authService) {
    this.authService = authService;
    // DOES NOT WORK BECAUSE OF ASPECTS
//    this.space = space;
  }

  public String getName() {
    return "snipsnap";
  }

   public boolean exists(String name) {
    return SnipSpaceFactory.getInstance().exists(name);
  }

  public boolean showCreate() {
    return authService.isAuthenticated(Application.get().getUser());
  }

  public void appendLink(StringBuffer buffer, String name, String view, String anchor) {
    SnipLink.appendLink(buffer, name, view, anchor);
  }

  public void appendLink(StringBuffer buffer, String name, String view) {
    SnipLink.appendLink(buffer, name, view);
  }

  public void appendCreateLink(StringBuffer buffer, String name, String view) {
    SnipLink.appendCreateLink(buffer, name);
  }

  public String include(String name) {
    Snip includeSnip = SnipSpaceFactory.getInstance().load(name);
    if (null != includeSnip) {
      return includeSnip.getContent();
    } else {
      return null;
    }
  }

  private String externalImageLink = null;
  public String getExternalImageLink() {
    if(null == externalImageLink) {
      Writer writer = new StringBufferWriter();
      try {
        SnipLink.appendImage(writer, "external-link", "&gt;&gt;");
      } catch (IOException e) {
        // ignore
      }
      externalImageLink = writer.toString();
    }
    return externalImageLink;
  }

  public String render(String content, RenderContext context) {
    init();
    FilterContext filterContext = new SnipFilterContext(((SnipRenderContext) context).getSnip());
    filterContext.setRenderContext(context);
    context.setRenderEngine(this);
    return fp.filter(content, filterContext);
  }
}
