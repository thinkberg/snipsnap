/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */

package examples;

import org.snipsnap.render.context.SnipRenderContext;
import org.snipsnap.render.macro.SnipMacro;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.user.User;

import java.io.IOException;
import java.io.Writer;

/**
 * Example which shows howto get the User from the context.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

// cut:start-1
public class GreetUserMacro extends SnipMacro {

  public void execute(Writer writer, SnipMacroParameter params)
    throws IllegalArgumentException, IOException {

    SnipRenderContext context = params.getSnipRenderContext();
    User user = (User) context.getAttribute("user");
    // Users which are not logged in are guests
    if (user.isGuest()) {
      writer.write("Hello, unknown friend.");
    } else {
      writer.write("Hello, " + user.getLogin());
    }
  }

  public String getName() {
    return "greet";
  }
}
// cut:end-1
