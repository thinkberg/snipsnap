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

import org.snipsnap.util.StringUtil;

import java.sql.Timestamp;

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
    StringBuffer buffer = new StringBuffer();
    buffer.append("Created by ");
    SnipLink.appendLink(buffer, cUser);
    buffer.append(". Last edited by ");
    SnipLink.appendLink(buffer, mUser);
    buffer.append(" ");
    buffer.append(getNiceTime(mTime));
    return buffer.toString();
  }

  /**
   * Return a short version of the modification user and time.
   * @return pretty print of the user and modified time
   */
  public String getShort() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<span class=\"snip-name\">");
    SnipLink.appendLink(buffer, cUser);
    buffer.append("</span>");
    buffer.append(", ");
    buffer.append(getNiceTime(mTime));
    return buffer.toString();
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
    if (time == null) { return ""; }
    java.util.Date now = new java.util.Date();
    long secs = (now.getTime() - time.getTime()) / 1000;
    //int sec = (int) secs % 60;
    long mins = secs / 60;
    int min = (int) mins % 60;
    long hours = mins / 60;
    int hour = (int) hours % 24;
    int days = (int) hours / 24;
    //int years = days / 365;

    StringBuffer nice = new StringBuffer();
    if (mins == 0) {
      nice.append("Just a blink of an eye ago.");
    } else if (hours == 0) {
      StringUtil.plural(nice, min, "minute");
      nice.append(" ago.");
    } else if (days == 0) {
      StringUtil.plural(nice, hour, "hour");
      nice.append(", ");
      StringUtil.plural(nice, min, "minute");
      nice.append(" ago.");
    } else {
      StringUtil.plural(nice, days, "day");
      nice.append(" ago.");
    }
    return nice.toString();
  }
}
