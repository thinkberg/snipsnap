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

package org.snipsnap.snip.storage;

import org.snipsnap.snip.Snip;

import java.util.List;
import java.sql.Timestamp;

/**
 * Interface that describes SnipStorage backends for SnipSnap
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public interface SnipStorage extends Storage {
  // Basic manipulation methods Load,Store,Create,Remove
  public Snip storageLoad(String name);

  public void storageStore(Snip snip);

  public Snip storageCreate(String name, String content);

  public void storageRemove(Snip snip);

  // Finder methods
  public int storageCount();

  public List storageAll();

  public List storageByHotness(int size);

  // find all Snips with the cUser matching the login
  public List storageByUser(String login);

  public List storageByDateSince(Timestamp date);

  public List storageByRecent(int size);

  public List storageByComments(Snip parent);

  // find all Snips matching the parent
  public List storageByParent(Snip parent);

  public List storageByParentNameOrder(Snip parent, int count);

  public List storageByParentModifiedOrder(Snip parent, int count);

  public List storageByDateInName(String start, String end);

}
