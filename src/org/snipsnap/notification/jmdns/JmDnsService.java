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

import org.picocontainer.Startable;
import org.snipsnap.app.Application;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceTypeListener;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

/**
 * Announce SnipSnaps with JmDns (like Apple Rendezvous)
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JmDnsService implements Startable, ServiceListener {

  private JmDNS jmdns;

  public JmDnsService() {
    try {
      jmdns = new JmDNS();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void stop() {
    jmdns.unregisterAllServices();
  }

  public void start() {
    try {
      Hashtable props = new Hashtable();
      props.put("url","index.html");
      jmdns.registerService(
          new ServiceInfo("_snipsnap._tcp.local.",
              Application.get().getConfiguration().getName()+"._snipsnap._tcp.local.",
              8668,
              0,
              0,
              props));
      jmdns.addServiceListener("_snipsnap._tcp.local.", this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List getServices() {
    return new ArrayList();
  }

  public void addService(JmDNS jmDNS, String type, String name) {
  }

  public void removeService(JmDNS jmDNS, String type, String name) {
  }

  public void resolveService(JmDNS jmDNS, String type, String name, ServiceInfo serviceInfo) {
  }

}
