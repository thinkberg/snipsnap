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

package snipsnap.api.snip;

import org.snipsnap.container.Components;
import snipsnap.api.snip.*;
import snipsnap.api.snip.SnipSpace;


/**
 * SnipSpaceFactory returns SnipSpace instances
 *
 * @author Stephan J. Schmidt
 * @version $Id: SnipSpaceFactory.java 915 2003-08-06 12:39:12Z stephan $
 */

public class SnipSpaceFactory {
  public static synchronized snipsnap.api.snip.SnipSpace getInstance() {
    return (SnipSpace) Components.getComponent(SnipSpace.class);
  }

  public static synchronized void removeInstance() {
  }
}
