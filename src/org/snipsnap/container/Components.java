package org.snipsnap.container;

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

import org.codehaus.nanning.config.AspectSystem;
import org.snipsnap.interceptor.custom.MissingSnipAspect;
import org.snipsnap.interceptor.custom.SnipSpaceACLAspect;
import org.picocontainer.PicoContainer;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.DefaultComponentFactory;
import org.picocontainer.hierarchical.HierarchicalPicoContainer;
import org.nanocontainer.nanning.NanningNanoContainer;

public class Components {
  public final static String DEFAULT_ENGINE = "defaultRenderEngine";

  private static PicoContainer container;

  public static synchronized PicoContainer getContainer() {
    if (null == container) {
      AspectSystem as = new AspectSystem();
      as.addAspect(new MissingSnipAspect());
      as.addAspect(new SnipSpaceACLAspect());

      //System.out.println("Creating PicoContainer ...");
      RegistrationPicoContainer c =  new HierarchicalPicoContainer.Default();

//     StringRegistrationNanoContainer c =
//          new StringRegistrationNanoContainerImpl(pc, Components.class.getClassLoader(), new StringToObjectConverter());

      try {
        NanningNanoContainer nc = new NanningNanoContainer(new DefaultComponentFactory(), c, as);
        //c.registerComponent("org.snipsnap.notification.NotificationService");
        nc.registerComponentByClass(org.snipsnap.snip.storage.JDBCUserStorage.class);
        nc.registerComponent(org.snipsnap.user.UserManager.class, org.snipsnap.user.DefaultUserManager.class);
        nc.registerComponent(org.snipsnap.user.AuthenticationService.class, org.snipsnap.user.DefaultAuthenticationService.class);
        nc.registerComponentByClass(org.snipsnap.user.PasswordService.class);
        nc.registerComponent(org.snipsnap.container.SessionService.class, org.snipsnap.container.DefaultSessionService.class);
        nc.registerComponent(DEFAULT_ENGINE, org.snipsnap.render.SnipRenderEngine.class);
        nc.registerComponentByClass(org.snipsnap.render.PlainTextRenderEngine.class);
        //nc.registerComponentByClass(org.snipsnap.snip.storage.JDBCSnipStorage.class);
        nc.registerComponentByClass(org.snipsnap.snip.storage.FileSnipStorage.class);
        nc.registerComponent(org.snipsnap.snip.SnipSpace.class, org.snipsnap.snip.SnipSpaceImpl.class);

        nc.registerComponent(org.snipsnap.xmlrpc.BloggerAPI.class, org.snipsnap.xmlrpc.BloggerHandler.class);
        nc.registerComponent(org.snipsnap.xmlrpc.MetaWeblogAPI.class, org.snipsnap.xmlrpc.MetaWeblogHandler.class);
        nc.instantiateComponents();

        container = nc;
      } catch (Exception e) {
        e.printStackTrace();
      }
      //System.out.println(" PicoContainer ok.");
    }

    return container;
  }

  public static Object getComponent(Class c) {
    return getContainer().getComponent(c);
  }

}
