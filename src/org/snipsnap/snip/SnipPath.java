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

package org.snipsnap.snip;

import java.io.IOException;
import java.io.Writer;

/**
 * Handle trees of snips with paths.
 *
 * @author stephan
 * @version $Id$
 */

public class SnipPath {
  private Snip snip;

  public SnipPath(Snip snip) {
     this.snip = snip;
  }

  //@TODO: make this a object not static, return object from Snip
  public Writer append(Writer writer, SnipSpace space) {
    String name = snip.getName();
    if (name.indexOf('/') == -1) {
      return writer;
    }
    String part = null;
    String snipName = null;
    int lastIndex = 0;
    int i = 0;
    int index = name.indexOf('/');
    try {
      while (index != -1 && i++ < 10) {
        part = name.substring(lastIndex, index);
        snipName = name.substring(0, index);
        if (space.exists(snipName)) {
          SnipLink.appendLink(writer, snipName, part);
        } else {
          writer.write(part);
        }
        lastIndex = index + 1;
        index = name.indexOf('/', lastIndex);
        writer.write(" > ");
      }
      // do not link last element of path because
      // we display the this element already
      writer.write(name.substring(lastIndex));
    } catch (IOException e) {
      System.out.println("SnipPath: Error writing to writer.");
    }
    return writer;
  }

}
