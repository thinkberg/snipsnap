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
import org.nanocontainer.nanning.NanningComponentAdapterFactory;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.snipsnap.app.ApplicationManager;
import org.snipsnap.app.ApplicationStorage;
import org.snipsnap.app.JDBCApplicationStorage;
import org.snipsnap.app.PropertyFileApplicationStorage;
import org.snipsnap.interceptor.custom.MissingSnipAspect;
import org.snipsnap.interceptor.custom.SnipSpaceACLAspect;
import org.snipsnap.jdbc.LazyDataSource;
import org.snipsnap.notification.MessageService;
import org.snipsnap.snip.storage.PropertyFileSnipStorage;
import org.snipsnap.snip.storage.SnipStorage;
import org.snipsnap.snip.storage.UserStorage;
import org.snipsnap.snip.storage.PropertyFileUserStorage;
import org.snipsnap.snip.storage.JDBCUserStorage;
import org.snipsnap.snip.storage.JDBCSnipStorage;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceImpl;
import org.snipsnap.versioning.DefaultVersionManager;
import org.snipsnap.versioning.DifferenceService;
import org.snipsnap.versioning.VersionManager;
import org.snipsnap.versioning.VersionStorage;
import org.snipsnap.versioning.JDBCVersionStorage;
import org.snipsnap.versioning.cookbook.CookbookDifferenceService;
import org.snipsnap.xmlrpc.GeneratorHandler;
import org.snipsnap.xmlrpc.SnipSnapHandler;
import org.snipsnap.xmlrpc.WeblogHandler;
import org.snipsnap.xmlrpc.WeblogsPingHandler;
import org.snipsnap.xmlrpc.MetaWeblogAPI;
import org.snipsnap.xmlrpc.BloggerAPI;
import org.snipsnap.xmlrpc.MetaWeblogHandler;
import org.snipsnap.xmlrpc.BloggerHandler;
import org.snipsnap.config.Globals;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.user.*;
import org.snipsnap.render.PlainTextRenderEngine;
import org.snipsnap.render.SnipRenderEngine;

import javax.sql.DataSource;

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

      Globals globals = ConfigurationProxy.getInstance();
      String database = globals.getDatabase();
      try {
        if("file".equals(database)) {
          nc.registerComponentImplementation(UserStorage.class, PropertyFileUserStorage.class);
          nc.registerComponentImplementation(SnipStorage.class, PropertyFileSnipStorage.class);
          nc.registerComponentImplementation(VersionStorage.class, PropertyFileSnipStorage.class);
          nc.registerComponentImplementation(ApplicationStorage.class, PropertyFileApplicationStorage.class);
        } else {
          nc.registerComponentInstance(DataSource.class, new LazyDataSource());
          nc.registerComponentImplementation(SnipStorage.class, JDBCSnipStorage.class);
          nc.registerComponentImplementation(UserStorage.class, JDBCUserStorage.class);
          nc.registerComponentImplementation(VersionStorage.class, JDBCVersionStorage.class);
          nc.registerComponentImplementation(ApplicationStorage.class, JDBCApplicationStorage.class);
        }
        nc.registerComponentImplementation(PermissionManager.class, DefaultPermissionManager.class);
        nc.registerComponentImplementation(UserManager.class, DefaultUserManager.class);
        nc.registerComponentImplementation(AuthenticationService.class, DefaultAuthenticationService.class);
        nc.registerComponentImplementation(PasswordService.class);
        nc.registerComponentImplementation(SessionService.class, DefaultSessionService.class);
        nc.registerComponentImplementation(DEFAULT_ENGINE, SnipRenderEngine.class);
        nc.registerComponentImplementation(PlainTextRenderEngine.class);
        nc.registerComponentImplementation(SnipSpace.class, SnipSpaceImpl.class);

        // Sec
        // XML-RPC Handlers
        nc.registerComponentImplementation(BloggerAPI.class, BloggerHandler.class);
        nc.registerComponentImplementation(MetaWeblogAPI.class, MetaWeblogHandler.class);

        nc.registerComponentImplementation(WeblogsPingHandler.class);
        nc.registerComponentImplementation(GeneratorHandler.class);
        nc.registerComponentImplementation(WeblogHandler.class);
        nc.registerComponentImplementation(SnipSnapHandler.class);

        //Others
        //nc.registerComponentImplementation(RegexService.class);
        nc.registerComponentImplementation(MessageService.class);
        nc.registerComponentImplementation(ApplicationManager.class);

        // Versioning
        nc.registerComponentImplementation(VersionManager.class, DefaultVersionManager.class);
        nc.registerComponentImplementation(DifferenceService.class, CookbookDifferenceService.class);

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
