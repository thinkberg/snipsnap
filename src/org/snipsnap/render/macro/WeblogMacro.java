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

package org.snipsnap.render.macro;

import org.snipsnap.render.filter.links.BackLinks;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.*;
import org.snipsnap.util.StringUtil;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.radeox.util.i18n.ResourceManager;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.text.MessageFormat;

/*
 * Macro that displays a weblog. All subsnips are read and
 * displayed in reverse chronological order.
 *
 * @author stephan
 * @version $Id$
 */

public class WeblogMacro extends SnipMacro {
  private SnipSpace space;
  public WeblogMacro() {
    space = SnipSpaceFactory.getInstance();
  }

  public String getName() {
    return "weblog";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.weblog.description");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.weblog.params").split(";");
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params.getLength() < 2) {
      int count = 0;
      if (params.getLength() == 1) {
        count = Integer.parseInt(params.get("0"));
      } else {
        count = 10;
      }

      String name = params.getSnipRenderContext().getSnip().getName();
      Blog blog = space.getBlog(name);

      // order by name
      // with correct ending /1,/2,/3,...,/11,/12
      List posts = blog.getPosts(count);
      //System.out.println("Weblog Posts for '"+name+"': "+posts.size());

      // Convert
      // - all Snips with start parent -> rename start/2003-05-02
      // - comments?

      // start/2002-03-01
      // start/2002-05-06
      // start/2002-05-06/1
      // start/2002-05-06/2
      //
      // 1. Group by day
      //    - iterate
      //    - cut "name/"
      //    - get day
      //    - if day changes, render new day
      // 2. Render each day

      int NAME_INDEX = 0;
      int DAY_INDEX = 1;
      int COUNT_INDEX = 2;

      String lastDay = "";
      Iterator iterator = posts.iterator();
      while (iterator.hasNext()) {
        Object object = iterator.next();
        // System.err.println("Class="+object.getClass());
        Snip entry = (Snip) object;

        String[] entryName = StringUtil.split(entry.getName(), "/");
        int slashOffset = entryName.length - 3;
        String day = (entryName.length > 1 ? entryName[slashOffset + DAY_INDEX] : entryName[0]);
        // New Day?
        //System.err.println("entryName="+Arrays.asList(entryName));
        if (!lastDay.equals(day)) {
          writer.write("<div class=\"blog-date\">");
          writer.write(SnipUtil.toDate(day));
          lastDay = day;
          writer.write("</div>");
        }

        Configuration conf = Application.get().getConfiguration();

        writer.write(entry.getXMLContent());
        writer.write(" <a href=\"");
        SnipLink.appendUrl(writer, entry.getName());
        writer.write("\" title=\"");
        MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "macro.anchor.permalink"));
        writer.write(mf.format(new Object[] { entry.getName() }));
        writer.write("\">");
        SnipLink.appendImage(writer, "Icon-Permalink", "PermaLink");
        writer.write("</a>");

        writer.write("<div class=\"snip-post-comments\">");
        writer.write(entry.getComments().getCommentString());
        writer.write(" | ");
        writer.write(entry.getComments().getPostString());
        writer.write("</div>\n\n");

        if ("true".equals(conf.getFeatureReferrerShow())) {
          writer.write("<div class=\"snip-backlinks\">");
          BackLinks.appendTo(writer, entry.getAccess().getBackLinks(), 5);
          writer.write("</div>");
        }
      }
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
