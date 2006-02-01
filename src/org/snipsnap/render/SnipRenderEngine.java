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

import org.radeox.api.engine.ImageRenderEngine;
import org.radeox.api.engine.IncludeRenderEngine;
import org.radeox.api.engine.WikiRenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.BaseRenderEngine;
import org.radeox.filter.context.FilterContext;
import org.radeox.util.Encoder;
import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import snipsnap.api.render.context.SnipRenderContext;
import org.snipsnap.render.filter.context.SnipFilterContext;
import org.snipsnap.serialization.StringBufferWriter;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.snip.SnipSpaceFactory;
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

//  private SnipSpace space;
  private AuthenticationService authService;

  public SnipRenderEngine(AuthenticationService authService) {
    this.authService = authService;
    //MacroRepository.getInstance().addLoader(new GroovyMacroLoader());
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
    return authService.isAuthenticated(snipsnap.api.app.Application.get().getUser());
  }

  public void appendLink(StringBuffer buffer, String name, String view, String anchor) {
    snipsnap.api.snip.SnipLink.appendLink(buffer, name, view, anchor);
  }

  public void appendLink(StringBuffer buffer, String name, String view) {
    snipsnap.api.snip.SnipLink.appendLink(buffer, name, view);
  }

  public void appendCreateLink(StringBuffer buffer, String name, String view) {
    Configuration config = snipsnap.api.app.Application.get().getConfiguration();
    String encodedSpace = config.getEncodedSpace();

    if (name.indexOf(encodedSpace.charAt(0)) == -1) {
      SnipLink.appendCreateLink(buffer, name);
    } else {
      buffer.append("&#91;<span class=\"error\">illegal '" + encodedSpace + "' in " + Encoder.escape(name) + "</span>&#93;");
    }
  }

  public String include(String name) {
    snipsnap.api.snip.Snip includeSnip = SnipSpaceFactory.getInstance().load(name);
    if (null != includeSnip) {
      return includeSnip.getContent();
    } else {
      return null;
    }
  }

  public String getExternalImageLink() {
      Writer writer = new StringBufferWriter();
      try {
        snipsnap.api.snip.SnipLink.appendImage(writer, "Icon-Extlink", "&gt;&gt;");
      } catch (IOException e) {
        // ignore
      }
    return writer.toString();
  }

  public String render(String content, RenderContext context) {
    init();
    FilterContext filterContext = new SnipFilterContext(((SnipRenderContext) context).getSnip());
    filterContext.setRenderContext(context);
    context.setRenderEngine(this);
    return fp.filter(content, filterContext);
  }
}
