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

import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.radeox.util.i18n.ResourceManager;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Helper class for dealing with snips
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipUtil {
  public static String toName(Date date) {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    return sf.format(date);
  }

  public static String toDate(String dateString) {
    Configuration config = Application.get().getConfiguration();

    int index = dateString.lastIndexOf('/');
    //@TODO: replace with regex check
    if (index != -1) {
      dateString = dateString.substring(index+1);
    }
    SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat out = new SimpleDateFormat(config.getWeblogDateFormat(),
                                                ResourceManager.getLocale("i18n.messages"));
    try {
      return out.format(in.parse(dateString));
    } catch (ParseException e) {
      return dateString;
    }
  }
}
