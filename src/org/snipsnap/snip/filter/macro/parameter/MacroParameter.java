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

package org.snipsnap.snip.filter.macro.parameter;

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.util.log.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Encapsulates parameters for an execute Macro call
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public interface MacroParameter {
  public void setParams(String stringParams);

  public String getContent();

  public void setContent(String content);

  public int getLength();

  public String get(String index, int idx);

  public String get(String index);

  public String get(int index);
}
