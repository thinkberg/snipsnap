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

import org.snipsnap.config.Configuration;
import org.snipsnap.snip.XMLSnipImport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class SetupTheme implements SetupHandler {
  public String getName() {
    return "theme";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String themeName = request.getParameter(Configuration.APP_THEME);
    if(config.isConfigured() && !ThemeHelper.getInstalledThemes().containsKey(themeName)) {
      try {
        File themeFile = (File) ThemeHelper.getThemeDocuments(config, ThemeHelper.FILES).get(themeName);
        XMLSnipImport.load(new FileInputStream(themeFile), XMLSnipImport.OVERWRITE | XMLSnipImport.IMPORT_SNIPS);
      } catch (IOException e) {
        errors.put(Configuration.APP_THEME, Configuration.APP_THEME);
        e.printStackTrace();
        return errors;
      }
    }
    config.setTheme(themeName);
    return errors;
  }

}
