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

package org.snipsnap.render.context;

import org.radeox.engine.context.BaseRenderContext;
import org.radeox.engine.context.RenderContext;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.app.Application;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * SnipRenderContext implements RenderContext and is used to
 * give special SnipSnap parameters to a SnipSnap aware
 * Rendering Engine
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipRenderContext extends BaseRenderContext {
  public static final String LANGUAGE_BUNDLE = "SnipRenderContext.language_bundle";

  private Snip snip;
  private SnipSpace space;

  public SnipRenderContext(Snip snip, SnipSpace space) {
    super();
    this.space = space;
    this.snip = snip;
    Locale locale = Application.get().getConfiguration().getLocale();
    //@TODO: optimize lookups perhaps with factory
    set(SnipRenderContext.LANGUAGE_BUNDLE, ResourceBundle.getBundle("i18n.messages", locale));
    set(RenderContext.LANGUAGE_LOCALE, locale);
  }

  /**
   * Gets the current snip for which the RenderEngine is called
   *
   * @return snip Snip for which the RenderEngine is called
   */
  public Snip getSnip() {
    return this.snip;
  }

  public SnipSpace getSpace() {
     return space;
  }

  public void setSpace(SnipSpace space) {
    this.space = space;
  }
}
