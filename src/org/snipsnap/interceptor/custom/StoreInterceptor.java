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

import org.radeox.util.logging.Logger;
import org.snipsnap.interceptor.InterceptorSupport;
import org.snipsnap.interceptor.Invocation;
import org.snipsnap.snip.Snip;

public class StoreInterceptor extends InterceptorSupport {
  public StoreInterceptor() {
    super();
  }

  public Object invoke(Invocation invocation) throws Throwable {
    if (invocation.getMethod().getName().startsWith("store")) {
      Snip snip = (Snip) invocation.getArgs()[0];
      if (snip.getName().startsWith("macro-")) {
        Logger.debug("Store on macro called.");
      }
    }
    return invocation.next();
  }
}
