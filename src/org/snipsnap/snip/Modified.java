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

import org.radeox.util.i18n.ResourceManager;
import snipsnap.api.container.Components;
import org.snipsnap.user.UserManager;

import java.sql.Timestamp;
import java.text.MessageFormat;

import snipsnap.api.snip.*;
import snipsnap.api.snip.SnipLink;

/**
 *  Object with modified information, e.g. for snips
 *  Modified can be pretty printed.
 *
 * @author stephan
 * @version $Id$
 */

public class Modified {
  String cUser, mUser;
  Timestamp cTime, mTime;

  public Modified(String cUser, String mUser, Timestamp cTime, Timestamp mTime) {
    this.cUser = cUser;
    this.mUser = mUser;
    this.cTime = cTime;
    this.mTime = mTime;
  }

  public Modified() {
  }

  public String getcUser() {
    return cUser;
  }

  public void setcUser(String cUser) {
    this.cUser = cUser;
  }

  public String getmUser() {
    return mUser;
  }

  public void setmUser(String mUser) {
    this.mUser = mUser;
  }

  public Timestamp getcTime() {
    return cTime;
  }

  public void setcTime(Timestamp cTime) {
    this.cTime = cTime;
  }

  public Timestamp getmTime() {
    return mTime;
  }

  public void setmTime(Timestamp mTime) {
    this.mTime = mTime;
  }

  /**
   * Generate a pretty print of the modified object,
   * e.g. "Created by steph - Last edited ..."
   *
   * @return Pretty print of modified object
   */
  public String toString() {
    MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "modified.info"),
                                         ResourceManager.getLocale("i18n.messages"));

    UserManager um = (UserManager) Components.getComponent(UserManager.class);
    return mf.format(new Object[]{
      um.exists(cUser) ? SnipLink.createLink(cUser) : cUser,
      um.exists(mUser) ? snipsnap.api.snip.SnipLink.createLink(mUser) : mUser,
      getNiceTime(mTime)
    });
  }

  /**
   * Return a short version of the modification user and time.
   * @return pretty print of the user and modified time
   */
  public String getShort() {
    MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "modified.info.short"),
                                         ResourceManager.getLocale("i18n.messages"));
    UserManager um = (UserManager) Components.getComponent(UserManager.class);
    return mf.format(new Object[] {
      um.exists(cUser) ? SnipLink.createLink(cUser) : cUser,
      getNiceTime(mTime)
    });
  }

  // Should go to a date class
  /**
   * Generate a pretty print of the difference
   * between the timestamp and now.
   * e.g. show minutes, minutes and hours and
   * days since now.
   * "3 hours, 5 minutes ago.", "4 days ago."
   *
   * @param time Timestamp to pretty print
   * @return Pretty string
   */
  public static String getNiceTime(Timestamp time) {
    if (time == null) {
      return "";
    }
    java.util.Date now = new java.util.Date();
    return getNiceTime(now.getTime(), time.getTime());

  }

  public static String getNiceTime(long now, long time) {
    long secs = (now - time) / 1000; // amount of seconds

    //int sec = (int) secs % 60;
    long mins = secs / 60;        // amount of minutes
    int min = (int) mins % 60;    // current minute within hour
    long hours = mins / 60;       // amount of hours
    int hour = (int) hours % 24;  // hour within day
    int days = (int) hours / 24;  // amount of days
    int day = days % 365;         // current day within the year
    int years = days / 365;       // amount of years

    StringBuffer nice = new StringBuffer();
    if (secs < 60) {
      nice.append(ResourceManager.getString("i18n.messages", "modified.time.just"));
    } else {
      if (hours == 0) {
        MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "modified.time.minutes"),
                                             ResourceManager.getLocale("i18n.messages"));
        mf.format(new Object[]{new Long(min)}, nice, null);
      } else if (days == 0) {
        MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "modified.time.hours"),
                                             ResourceManager.getLocale("i18n.messages"));
        mf.format(new Object[]{new Long(hour), new Long(min)}, nice, null);
      } else if(years == 0) {
        MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "modified.time.days"),
                                             ResourceManager.getLocale("i18n.messages"));
        mf.format(new Object[]{new Long(days)}, nice, null);
      } else {
        MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "modified.time.years"),
                                             ResourceManager.getLocale("i18n.messages"));
        mf.format(new Object[]{new Long(years), new Long(day)}, nice, null);
      }
    }
    return nice.toString();
  }
}
