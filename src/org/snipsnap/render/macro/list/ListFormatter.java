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
package org.snipsnap.render.macro.list;

import org.radeox.util.Linkable;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * List formatter interface. List formatters are loaded via the plugin mechanism.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public interface ListFormatter {
  public String getName();

  /**
   * Display a simple vertical list.
   *
   * @param writer Writer to write the list output to
   * @param current the current linkable
   * @param listComment String to display before the list
   * @param c Collection of Linkables, Snips or Nameables to display
   * @param emptyText Text to display if collection is empty
   * @param showSize If showSize is true then the size of the collection is displayed
   */
  public void format(Writer writer,
                     Linkable current,
                     String listComment,
                     Collection c,
                     String emptyText,
                     boolean showSize)
      throws IOException;
}
