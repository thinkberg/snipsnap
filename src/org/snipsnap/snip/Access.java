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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.Links;

/**
 * Stores Access information for a snip like viewCount, backLinks, ...
 *
 * @author stephan
 * @version $Id$
 */

public class Access {
  private Snip snip;
  private Links backLinks, snipLinks;
  private int viewCount = 0;
  private boolean isModified;

  public Access(Snip snip) {
    this.snip = snip;
  }

  public Access(Snip snip, Links backLinks, Links snipLinks, int viewCount) {
    this.snip = snip;
    this.backLinks = backLinks;
    this.snipLinks = snipLinks;
    this.viewCount = viewCount;
  }

  public boolean isModified() {
    return isModified;
  }

  public void addLink(String url) {
    isModified = true;
    snipLinks.addLink(url);
  }

  public void setSnip(Snip snip) {
    isModified = true;
    this.snip = snip;
  }

  public Links getBackLinks() {
    return backLinks;
  }

  public void setBackLinks(Links backLinks) {
    isModified = true;
    this.backLinks = backLinks;
  }

  public Links getSnipLinks() {
    return snipLinks;
  }

  public void setSnipLinks(Links snipLinks) {
    isModified = true;
    this.snipLinks = snipLinks;
  }

  public int getViewCount() {
    return viewCount;
  }

  public void setViewCount(int viewCount) {
    isModified = true;
    this.viewCount = viewCount;
  }

  public int incViewCount() {
    return ++this.viewCount;
  }
}
