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

package snipsnap.api.label;

import snipsnap.api.snip.Snip;
import org.snipsnap.snip.label.Indexable;
import org.snipsnap.snip.label.LifeCycle;

import java.util.Map;

/**
 * Label is attached to Snips
 * @author Stephan J. Schmidt
 * @version $Id: Label.java 1263 2003-12-12 08:58:25Z stephan $
 */
public interface Label extends Indexable, LifeCycle {
  // public String serialize();

  // public void deserialize(String label);

  public String getListProxy();

  public String getInputProxy();

  public void handleInput(Map input);

  public String getType();

  public String getName();

  public String getValue();

  public void setName(String name);

  public void setValue(String value);

  public void setSnip(snipsnap.api.snip.Snip snip);

  public snipsnap.api.snip.Snip getSnip();

  public snipsnap.api.label.LabelContext getContext();
}
