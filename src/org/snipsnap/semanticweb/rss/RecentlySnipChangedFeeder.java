/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */

package org.snipsnap.semanticweb.rss;

import org.snipsnap.snip.Blog;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.app.Application;
import org.snipsnap.feeder.Feeder;
import org.snipsnap.feeder.FeederContext;

import java.util.List;

/*
 * Generates a feed of recently changed snips which can then be
 * displayed or serialized to RSS, RDF, Atom, ...
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class RecentlySnipChangedFeeder implements Feeder {
  private SnipSpace space;

  public RecentlySnipChangedFeeder() {
    space = SnipSpaceFactory.getInstance();
  }

  public String getName() {
    return "recentlychanged";
  }

  public List getFeed(FeederContext context, int count) {
    List changed = space.getChanged(count);
    return changed;
  }

  public List getFeed(FeederContext context) {
    return getFeed(context, 10);
  };

  public Snip getContextSnip() {
    String startName = Application.get().getConfiguration().getStartSnip();
    return space.load(startName);
  }
}
