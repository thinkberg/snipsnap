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

import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.XMLSnipExport;
import org.snipsnap.snip.storage.SnipSerializer;
import org.radeox.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

public class Maintenance implements SetupHandler {
  public String getName() {
    return "maintenance";
  }

  private Map workerThreads = new HashMap();

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String appOid = (String) Application.get().getObject(Application.OID);
    CheckConsistency workerThread = (CheckConsistency) workerThreads.get(appOid);
    if (workerThread != null && workerThread.isAlive()) {
      setRunning(workerThread, request.getSession());
      return errors;
    }

    if (request.getParameter("check") != null) {
      if (workerThread != null) {
        workerThreads.remove(appOid);
        workerThread = null;
      }
      workerThread = new CheckConsistency(appOid, false);
      workerThread.start();
      workerThreads.put(appOid, workerThread);

      setRunning(workerThread, request.getSession());
    } else if(request.getParameter("dorepair") != null) {
      CheckConsistency repairThread = new CheckConsistency(appOid, true);
      repairThread.setParentsToFix(workerThread.getParentsToFix());
      repairThread.setCommentsToFix(workerThread.getCommentsToFix());
      repairThread.setDuplicates(workerThread.getDuplicates());
      repairThread.start();
      workerThreads.put(appOid, repairThread);

      setRunning(repairThread, request.getSession());
    } else {
      request.getSession().removeAttribute("running");
      if(workerThread != null && !workerThread.isAlive()) {
        request.setAttribute("fixComments", workerThread.getCommentsToFix());
        request.setAttribute("fixParents", workerThread.getParentsToFix());
        request.setAttribute("duplicates", workerThread.getDuplicates());
        request.setAttribute("notFixable", workerThread.getNonFixable());
      }
    }

    return errors;
  }

  private void setRunning(CheckConsistency workerThread, HttpSession session) {
    Map statusMap = (Map)session.getAttribute("running");
    if(null == statusMap) {
      statusMap = new HashMap();
    }
    statusMap.put("max", new Integer(workerThread.getMax()));
    statusMap.put("current", new Integer(workerThread.getCurrent()));
    if(workerThread.isRepairing()) {
      statusMap.put("key", "repairing");
    } else {
      statusMap.put("key", "checking");
    }
    session.setAttribute("running", statusMap);
  }

  class CheckConsistency extends Thread {
    private String appOid = null;
    private List fixDuplicates = new ArrayList();
    private List fixComments = new ArrayList();
    private List fixParents = new ArrayList();
    private List noFix = new ArrayList();

    private boolean repair = false;

    private int currentCount = 0;
    private int snipCount = 0;

    public CheckConsistency(String appOid, boolean repair) {
      super();
      this.appOid = appOid;
      this.repair = repair;
    }

    public boolean isRepairing() {
      return repair;
    }

    public void run() {
      Application.get().storeObject(Application.OID, appOid);
      SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);

      if (!repair) {
        List allSnips = Collections.unmodifiableList(space.getAll());
        Set uniqeSnipNames = new HashSet();
        Iterator snipIt = allSnips.iterator();
        snipCount = allSnips.size();
        Logger.debug("Need to check " + snipCount + " snips.");
        while (snipIt.hasNext()) {
          Snip snip = (Snip) snipIt.next();
          check(snip, space);
          if(!uniqeSnipNames.add(snip.getName())) {
            fixDuplicates.add(snip);
          }
          currentCount++;
        }
      } else {
        snipCount = fixParents.size() + fixComments.size() + fixDuplicates.size();
        currentCount = 0;
        Iterator parentIt = fixParents.iterator();
        while (parentIt.hasNext()) {
          Snip snip = (Snip) parentIt.next();
          fixParent(snip);
          space.systemStore(snip);
          currentCount++;
          parentIt.remove();
        }
        Iterator commentIt = fixComments.iterator();
        while (commentIt.hasNext()) {
          Snip snip = (Snip) commentIt.next();
          fixComment(snip, space);
          space.systemStore(snip);
          currentCount++;
          commentIt.remove();
        }
      }
    }

    public void setCommentsToFix(List snips) {
      fixComments = snips;
    }

    public List getCommentsToFix() {
      return fixComments;
    }

    public void setParentsToFix(List snips) {
      fixParents = snips;
    }

    public List getParentsToFix() {
      return fixParents;
    }

    public void setDuplicates(List snips) {
      fixDuplicates = snips;
    }

    public List getDuplicates() {
      return fixDuplicates;
    }

    public List getNonFixable() {
      return noFix;
    }

    private void check(Snip snip, SnipSpace space) {
      String snipName = snip.getName();
      if (snipName.startsWith("comment-")) {
        if (null == snip.getCommentedSnip() && (null == snip.getCommentedName() || "".equals(snip.getCommentedName()))) {
          String commentedName = snipName.substring("comment-".length(), snipName.lastIndexOf("-"));
          if (!space.exists(commentedName)) {
            Logger.warn("non-fixable snip found: '" + snipName + "' (commented snip '" + commentedName + "' missing)");
            noFix.add(snip);
          } else {
            Logger.warn("snip '" + snipName + "' is missing its commented snip '" + commentedName + "'");
            fixComments.add(snip);
          }
        }
      }

      if (snipName.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
        if (null == snip.getParent() && (null == snip.getParentName() || "".equals(snip.getParentName()))) {
          Logger.warn("snip '" + snipName + "' is missing its parent snip");
          fixParents.add(snip);
        }
      }
    }

    private void fixParent(Snip snip) {
      String snipName = snip.getName();
      if (snipName.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
        if (null == snip.getParent() && (null == snip.getParentName() || "".equals(snip.getParentName()))) {
          Logger.warn("fixing snip parent of '" + snipName + "'");
          snip.setParentName("start");
        }
      }
    }

    private void fixComment(Snip snip, SnipSpace space) {
      String snipName = snip.getName();
      if (snipName.startsWith("comment-")) {
        if (null == snip.getCommentedSnip() && (null == snip.getCommentedName() || "".equals(snip.getCommentedName()))) {
          String commentedName = snipName.substring("comment-".length(), snipName.lastIndexOf("-"));
          if (!space.exists(commentedName)) {
            Logger.warn("commented snip of snip '" + snipName + "' got lost?");
          } else {
            Logger.warn("fixing commented snip '" + snipName + "' -> '" + commentedName + "'");
            snip.setCommentedName(commentedName);
          }
        }
      }
    }

    public int getMax() {
      return snipCount;
    }

    public int getCurrent() {
      return currentCount;
    }
  }
}

