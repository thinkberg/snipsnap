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

/**
 * Stores versions of snips
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public interface VersionStorage {

  /**
   * Return a list of VersionInfo objects for the
   * given snip. Objects should be ordered by decreasing version
   *
   * @param snip Snip for which the revision should be loaded
   * @return
   */
  public List getVersionHistory(Snip snip);

  /**
   * Load a version of a snip from the storage
   *
   * @param snip Example of a snip to load
   * @param version Version number
   * @return
   */
  public Snip loadVersion(Snip snip, int version);

  /**
   * Stora a version of a snip in the storage.
   *
   * @param snip Snip to store
   */
  public void storeVersion(Snip snip);
}
