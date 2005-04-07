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
package org.snipsnap.snip;

import dynaop.Proxy;
import dynaop.ProxyAware;
import gabriel.Principal;
import org.picocontainer.PicoContainer;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import snipsnap.api.container.Components;
import snipsnap.api.render.context.SnipRenderContext;
import org.snipsnap.snip.attachment.Attachment;
import org.snipsnap.snip.attachment.Attachments;
import snipsnap.api.label.Labels;
import org.snipsnap.snip.label.RenderEngineLabel;
import org.snipsnap.snip.name.NameFormatter;
import org.snipsnap.snip.name.PathRemoveFormatter;
import org.snipsnap.snip.name.WeblogNameFormatter;
import org.snipsnap.user.Permissions;
import snipsnap.api.user.User;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

/**
 * Content of a snip.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Content {
  private String text;

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

}
