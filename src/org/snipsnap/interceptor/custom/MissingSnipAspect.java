package org.snipsnap.interceptor.custom;

/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002-2003 Stephan J. Schmidt, Matthias L. Jugel
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

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.P;
import org.codehaus.nanning.config.Pointcut;
import org.snipsnap.snip.SnipSpace;

import java.util.HashMap;
import java.util.Map;

/**
 * MissingLSnip Aspect caches method call result
 * from SnipSpace (exists, create).
 * When a missing snip is detected the
 * snip name is stored. When the snip is created
 * the snip is removed from the missing list.
 *
 * @author stephan
 * @version $Id$
 */

public class MissingSnipAspect implements Aspect {
  Pointcut existsPc = P.methodName("exists.*");
  Pointcut createPc = P.methodName("create.*");
  Pointcut removePc = P.methodName("remove.*");

  private Map missing;
  private Map existing;

  public MissingSnipAspect() {
    this.missing = new HashMap();
    this.existing = new HashMap();
  }

  public void advise(AspectInstance instance) {
    Class klass = instance.getClassIdentifier();
//    System.out.println("class="+klass);
//    System.out.println("instance="+instance);
    if (klass != null && klass.equals(SnipSpace.class)) {
      existsPc.advise(instance, new MethodInterceptor() {
        public Object invoke(Invocation invocation) throws Throwable {
          String name = ((String)
              invocation.getArgs()[0]).toUpperCase();
          // Snip is in the missing list
          if (missing.containsKey(name)) {
            //System.out.println("Hit=" + name);
            return new Boolean(false);
          } else if (existing.containsKey(name)) {
            return new Boolean(true);
          }

          //System.out.println("Miss=" + name);
          Boolean result = (Boolean)
              invocation.invokeNext();
          //System.out.println("Result=" + name + " exists?=" + result);
          // The snip does not exist so put it in the missing list
          if (result.equals(Boolean.FALSE)) {
            missing.put(name, new Integer(0));
          } else {
            existing.put(name, new Integer(0));
          }
          return result;
        }
      });


      removePc.advise(instance, new MethodInterceptor() {
        public Object invoke(Invocation invocation) throws Throwable {
          String name = ((String) invocation.getArgs()[0]).toUpperCase();

          Object result = invocation.invokeNext();

          if (existing.containsKey(name)) {
            existing.remove(name);
          }
          return result;
        }
      });

      createPc.advise(instance, new MethodInterceptor() {
        public Object invoke(Invocation invocation) throws Throwable {
          String name = ((String) invocation.getArgs()[0]).toUpperCase();

          Object result = invocation.invokeNext();

          if (missing.containsKey(name)) {
            missing.remove(name);
          }
          return result;
        }
      });
    }
  }

  public void introduce(AspectInstance instance) {
  }
}
