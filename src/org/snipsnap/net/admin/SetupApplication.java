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
import org.snipsnap.config.InitializeDatabase;
import org.snipsnap.net.FileUploadServlet;
import org.snipsnap.net.filter.MultipartWrapper;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class SetupApplication implements SetupHandler {
  private FileUploadServlet uploader = new FileUploadServlet();

  public String getName() {
    return "application";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    config.setName(request.getParameter(Configuration.APP_NAME));
    config.setTagline(request.getParameter(Configuration.APP_TAGLINE));
    if (request instanceof MultipartWrapper) {
      try {
        if (config.isConfigured()) {
          SnipSpace space = SnipSpaceFactory.getInstance();
          Snip snip = space.load(Configuration.SNIPSNAP_CONFIG);
          String logoName = uploader.uploadFile(request, snip, config.getFilePath());
          if(logoName != null && !"".equals(logoName)) {
            config.setLogo(logoName);
          }
        } else {
          MultipartWrapper mpRequest = (MultipartWrapper) request;
          String fileName = mpRequest.getFileName("file");
          if (fileName != null && !"".equals(fileName)) {
            String logoFileName = uploader.getCanonicalFileName(mpRequest.getFileName("file"));
            String logoFileType = mpRequest.getFileContentType("file");
            if (logoFileType.startsWith("image")) {
              InputStream logoFileIs = mpRequest.getFileInputStream("file");
              File logoFile = File.createTempFile("SnipSnapLogo", null);
              FileOutputStream imageOut = new FileOutputStream(logoFile);
              byte buffer[] = new byte[8192];
              int len = 0;
              while ((len = logoFileIs.read(buffer)) != -1) {
                imageOut.write(buffer, 0, len);
              }
              imageOut.close();
              logoFileIs.close();
              config.setLogo(logoFileName);
              config.set(InitializeDatabase.LOGO_FILE, logoFile.getPath());
              config.set(InitializeDatabase.LOGO_FILE_TYPE, logoFileType);
            } else {
              errors.put(Configuration.APP_LOGO, Configuration.APP_LOGO + ".type");
            }
          }

        }
      } catch (IOException e) {
        errors.put(Configuration.APP_LOGO, Configuration.APP_LOGO);
        e.printStackTrace();
      }
    }

//    String usage = request.getParameter("usage");
//    if ("public".equals(usage)) {
//      config.setPermRegister("allow");
//      config.setPermWeblogsPing("allow");
//    } else if ("closed".equals(usage)) {
//      config.setPermRegister("deny");
//      config.setPermWeblogsPing("deny");
//    } else if ("intranet".equals(usage)) {
//      config.setPermWeblogsPing("deny");
//    } else {
//      if (!steps.contains(STEP_PERMISSIONS)) {
//        steps.add(steps.size() - 1, STEP_PERMISSIONS);
//      }
//      request.getSession().setAttribute(ATT_USAGE, "custom");
//    }

    String name = config.getName();
    if (null == name || "".equals(name)) {
      errors.put(Configuration.APP_NAME, Configuration.APP_NAME);
    }

    return errors;
  }
}
