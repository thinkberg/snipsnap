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
 * Manages revisions for snips
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class DefaultVersionManager implements VersionManager {
  private VersionStorage storage;
  private DifferenceService service;

  public DefaultVersionManager(VersionStorage storage, DifferenceService service) {
    this.storage = storage;
    this.service = service;
  }

  public void storeVersion(Snip snip) {
    // Consistently increase the version of the snip
    storage.storeVersion(snip);
  };

  public Snip loadVersion(Snip snip, int version) {
    return storage.loadVersion(snip, version);
  };

  public List diff(Snip snip, int version1, int version2) {
    System.err.println("old="+version1);
    System.err.println("new="+version2);
    return service.diff(loadVersion(snip, version1).getContent(), loadVersion(snip, version2).getContent());
  }

  public List getHistory(Snip snip) {
    return storage.getVersionHistory(snip);
  }
}
