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

package org.snipsnap.notification.jmdns;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

/**
 * Announce SnipSnaps with JmDns (like Apple Rendezvous)
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JmDnsService {
  public JmDnsService() {
    try {
      JmDNS jmdns = new JmDNS();
      jmdns.registerService(
          new ServiceInfo("_http._tcp.local.", "snipsnap._http._tcp.local.", 8668, 0, 0, "path=index.html")
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
