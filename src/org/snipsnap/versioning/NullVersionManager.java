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

package org.snipsnap.versioning;

import org.snipsnap.snip.Snip;

import java.util.List;
import java.util.ArrayList;

/**
 * VersionManager that does nothing, if the user
 * does not want versioning
 *
 * Just return the snip or empty lists, noone should call these
 * as the GUI for version etc should be turned off. Some sane
 * values for those who do call this.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class NullVersionManager implements VersionManager {

  public NullVersionManager() {
  }

  public void storeVersion(Snip snip) {
  };

  public Snip loadVersion(Snip snip, int version) {
    return snip;
  };

  public List diff(Snip snip, int version1, int version2) {
    return new ArrayList();
  }

  public List getHistory(Snip snip) {
    return new ArrayList();
  }
}
