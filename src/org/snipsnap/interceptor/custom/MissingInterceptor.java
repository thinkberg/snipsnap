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

package org.snipsnap.interceptor.custom;

import dynaop.DispatchInterceptor;
import snipsnap.api.snip.Snip;
import org.snipsnap.util.ApplicationAwareMap;

import java.util.HashMap;

/**
 * Interceptor for caching SnipSpace misses. SnipSnap
 * often calls exists() to detect if a snip exists.
 * This operation can be costly so we keep the results
 * to exists() cached.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class MissingInterceptor extends DispatchInterceptor {
  // @TODO we use map instead of set to increment the
  // missing counter each time a snip could not be found
  private ApplicationAwareMap missing;
  private ApplicationAwareMap existing;


  public MissingInterceptor() {
    super();
    this.missing = new ApplicationAwareMap(HashMap.class, HashMap.class);
    this.existing = new ApplicationAwareMap(HashMap.class, HashMap.class);
  }


  public boolean exists(String name) throws Throwable {
      // Snip is in the missing list
      if (missing.getMap().containsKey(name)) {
        //System.out.println("Hit=" + name);
        return false;
      } else if (existing.getMap().containsKey(name)) {
        return true;
      }

      //System.out.println("Miss=" + name);
      Boolean result = (Boolean) proceed();
      // System.out.println("Result=" + name + " exists?=" + result);
      // The snip does not exist so put it in the missing list
      if (result.equals(Boolean.FALSE)) {
        missing.getMap().put(name, new Integer(0));
      } else {
        existing.getMap().put(name, new Integer(0));
      }
      return result.booleanValue();
  }

  public snipsnap.api.snip.Snip create(String name, String content) throws Throwable {
    Object result = proceed();

    if (missing.getMap().containsKey(name)) {
      missing.getMap().remove(name);
    }
    return (Snip) result;
  }

  public void remove(snipsnap.api.snip.Snip snip) throws Throwable {
    String name = snip.getName().toUpperCase();

    Object result = proceed();

    if (existing.getMap().containsKey(name)) {
      existing.getMap().remove(name);
    }
    return;
  }

}
