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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SetupLocalization implements SetupHandler {
  public String getName() {
    return "localization";
  }

  private final static List countries = Arrays.asList(Locale.getISOCountries());
  private final static List languages = Arrays.asList(Locale.getISOLanguages());

  public Map setup(HttpServletRequest request, HttpServletResponse response, snipsnap.api.config.Configuration config, Map errors) {
    String country = request.getParameter(Configuration.APP_COUNTRY);
    if (countries.contains(country)) {
      config.setCountry(country);
    } else {
      errors.put(snipsnap.api.config.Configuration.APP_COUNTRY, Configuration.APP_COUNTRY);
    }

    String language = request.getParameter(Configuration.APP_LANGUAGE);
    if (languages.contains(language)) {
      config.setLanguage(language);
    } else {
      errors.put(snipsnap.api.config.Configuration.APP_LANGUAGE, Configuration.APP_LANGUAGE);
    }
    config.setTimezone(request.getParameter(snipsnap.api.config.Configuration.APP_TIMEZONE));
    config.setWeblogDateFormat(request.getParameter(snipsnap.api.config.Configuration.APP_WEBLOGDATEFORMAT));
    try {
      DateFormat df = new SimpleDateFormat(config.getWeblogDateFormat());
      df.format(new Date());
    } catch (Exception e) {
      errors.put(snipsnap.api.config.Configuration.APP_WEBLOGDATEFORMAT, snipsnap.api.config.Configuration.APP_WEBLOGDATEFORMAT);
    }
    String geoCoordinates = request.getParameter(Configuration.APP_GEOCOORDINATES);
    if (null != geoCoordinates && !"".equals(geoCoordinates)) {
      config.setGeoCoordinates(geoCoordinates);
      int commaIdx = geoCoordinates.indexOf(',');
      if (commaIdx > 0) {
        String latStr = geoCoordinates.substring(0, commaIdx).trim();
        String lonStr = geoCoordinates.substring(commaIdx + 1).trim();
        if (latStr.length() == 0 || lonStr.length() == 0) {
          errors.put(snipsnap.api.config.Configuration.APP_GEOCOORDINATES, Configuration.APP_GEOCOORDINATES);
        } else {
          try {
            double latitude = Double.parseDouble(latStr);
            double longitude = Double.parseDouble(lonStr);
            if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
              config.setGeoCoordinates(geoCoordinates);
            } else {
              errors.put(snipsnap.api.config.Configuration.APP_GEOCOORDINATES, snipsnap.api.config.Configuration.APP_GEOCOORDINATES + ".range");
            }
          } catch (NumberFormatException e) {
            errors.put(Configuration.APP_GEOCOORDINATES, snipsnap.api.config.Configuration.APP_GEOCOORDINATES + ".format");
            e.printStackTrace();
          }
        }
      } else {
        errors.put(Configuration.APP_GEOCOORDINATES, snipsnap.api.config.Configuration.APP_GEOCOORDINATES);
      }
    }
    return errors;
  }
}
