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

import org.snipsnap.interceptor.custom.MissingSnipAspect;
import org.snipsnap.interceptor.custom.SnipSpaceACLAspect;
import org.snipsnap.xmlrpc.WeblogsPingHandler;
import org.snipsnap.xmlrpc.GeneratorHandler;
import org.snipsnap.xmlrpc.WeblogHandler;
import org.snipsnap.xmlrpc.SnipSnapHandler;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;

import org.nanocontainer.nanning.NanningComponentAdapterFactory;
import org.codehaus.nanning.config.AspectSystem;

public class Components {
  public final static String DEFAULT_ENGINE = "defaultRenderEngine";

  private static PicoContainer container;

  public static synchronized PicoContainer getContainer() {
    if (null == container) {
      //System.out.println("Creating PicoContainer ...");
      DefaultPicoContainer nc = new DefaultPicoContainer(
            new NanningComponentAdapterFactory(
            new AspectSystem(),
            new DefaultComponentAdapterFactory()));

      nc.registerComponentImplementation(MissingSnipAspect.class);
      nc.registerComponentImplementation(SnipSpaceACLAspect.class);
      nc.getComponentInstances();

//     StringRegistrationNanoContainer c =
//          new StringRegistrationNanoContainerImpl(pc, Components.class.getClassLoader(), new StringToObjectConverter());

      try {
        //c.registerComponent("org.snipsnap.notification.NotificationService");
        nc.registerComponentImplementation(org.snipsnap.snip.storage.JDBCUserStorage.class);
        nc.registerComponentImplementation(org.snipsnap.user.UserManager.class, org.snipsnap.user.DefaultUserManager.class);
        nc.registerComponentImplementation(org.snipsnap.user.AuthenticationService.class, org.snipsnap.user.DefaultAuthenticationService.class);
        nc.registerComponentImplementation(org.snipsnap.user.PasswordService.class);
        nc.registerComponentImplementation(org.snipsnap.container.SessionService.class, org.snipsnap.container.DefaultSessionService.class);
        nc.registerComponentImplementation(DEFAULT_ENGINE, org.snipsnap.render.SnipRenderEngine.class);
        nc.registerComponentImplementation(org.snipsnap.render.PlainTextRenderEngine.class);
        nc.registerComponentImplementation(org.snipsnap.snip.storage.JDBCSnipStorage.class);
        //nc.registerComponentByClass(org.snipsnap.snip.storage.FileSnipStorage.class);
        nc.registerComponentImplementation(org.snipsnap.snip.SnipSpace.class, org.snipsnap.snip.SnipSpaceImpl.class);

        // XML-RPC Handlers
        nc.registerComponentImplementation(org.snipsnap.xmlrpc.BloggerAPI.class, org.snipsnap.xmlrpc.BloggerHandler.class);
        nc.registerComponentImplementation(org.snipsnap.xmlrpc.MetaWeblogAPI.class, org.snipsnap.xmlrpc.MetaWeblogHandler.class);

        nc.registerComponentImplementation(WeblogsPingHandler.class);
        nc.registerComponentImplementation(GeneratorHandler.class);
        nc.registerComponentImplementation(WeblogHandler.class);
        nc.registerComponentImplementation(SnipSnapHandler.class);

        container = nc;
      } catch (Exception e) {
        e.printStackTrace();
      }
      //System.out.println(" PicoContainer ok.");
    }

    return container;
  }

  public static Object getComponent(Class c) {
    return getContainer().getComponentInstance(c);
  }

}
