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

import org.radeox.ImageRenderEngine;
import org.radeox.IncludeRenderEngine;
import org.radeox.RenderEngine;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;


/**
 * RenderEngine implementation for SnipSnap which understoods e.g.
 * howto include other snips.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipRenderEngine extends RenderEngine implements IncludeRenderEngine, ImageRenderEngine {

  public String getName() {
    return "snipsnap";
  }

  // Register this RenderEngine with the RenderEngine manager
  static {
    org.radeox.RenderEngine.registerEngine(new SnipRenderEngine());
  }

  public String include(String name) {
    Snip includeSnip = SnipSpace.getInstance().load(name);
    if (null != includeSnip) {
      return includeSnip.getContent();
    } else {
      return null;
    }
  }

  public String getExternalImageLink() {
    return SnipLink.createImage("external-link", "&gt;&gt;");
  }
}
