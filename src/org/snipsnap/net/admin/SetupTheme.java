/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
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
package org.snipsnap.net.admin;

import snipsnap.api.config.Configuration;
import snipsnap.api.container.Components;
import snipsnap.api.snip.SnipSpace;
import org.snipsnap.snip.XMLSnipExport;
import org.snipsnap.snip.XMLSnipImport;
import org.snipsnap.snip.storage.SnipSerializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SetupTheme implements SetupHandler {
  public String getName() {
    return "theme";
  }

  List ignoreElements = Arrays.asList(new String[]{
    SnipSerializer.SNIP_APPLICATION,
    SnipSerializer.SNIP_BACKLINKS,
    SnipSerializer.SNIP_CUSER,
    SnipSerializer.SNIP_MUSER,
    SnipSerializer.SNIP_OUSER,
    SnipSerializer.SNIP_PARENT,
    SnipSerializer.SNIP_COMMENTED,
    SnipSerializer.SNIP_SNIPLINKS,
    SnipSerializer.SNIP_VERSION,
    SnipSerializer.SNIP_VIEWCOUNT
  });

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String themeName = request.getParameter(Configuration.APP_THEME);

    if (config.isConfigured() ) {
      if (request.getParameter("export") != null && ThemeHelper.getInstalledThemes().containsKey(themeName)) {
        SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
        List snips = Arrays.asList(space.match("SnipSnap/themes/" + themeName));

        response.setContentType("text/xml");
        try {
          XMLSnipExport.store(response.getOutputStream(), snips, null, null, ignoreElements, config.getFilePath());
          return null;
        } catch (IOException e) {
          errors.put("config.theme.export", "config.theme.export");
          return errors;
        }
      } else if(!ThemeHelper.getInstalledThemes().containsKey(themeName)) {
        try {
          File themeFile = (File) ThemeHelper.getThemeDocuments(config, ThemeHelper.FILES).get(themeName);
          XMLSnipImport.load(new FileInputStream(themeFile), XMLSnipImport.OVERWRITE | XMLSnipImport.IMPORT_SNIPS);
        } catch (IOException e) {
          errors.put(Configuration.APP_THEME, Configuration.APP_THEME);
          e.printStackTrace();
          return errors;
        }
      }
    }

    config.setTheme(themeName);
    return errors;
  }

}
