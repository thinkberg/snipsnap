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

package org.snipsnap.snip.storage;

import org.radeox.util.logging.Logger;
import org.snipsnap.snip.Snip;

import java.io.*;
import java.util.Map;

/**
 * SnipStorage backend that uses files for persisting data. This storage
 * has limitations in the snip name length and possibly characters as well
 * since not all filesystems can store UTF-8 file names.
 *
 * This Storage uses one file for persistance.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public abstract class OneFileSnipStorage extends FileSnipStorage {

  /**
   * Load the snip from an InputStream and
   * store the metadata and content in a Map.
   *
   * @param in Stream to read from
   * @return
   * @throws IOException
   */
  protected abstract Map loadSnip(InputStream in) throws IOException;

  /**
   * Return the file name of the snip file. This should
   * be implemented to return eg. snip.xml
   *
   * @return
   */
  protected abstract String getFileName();

  /**
   * Store the snip to
   * the output stream. Implementations
   * can store whichever format they choose
   * (plain text, XML, ...)
   *
   * @param snip
   * @param out
   */

  protected abstract void storeSnip(Snip snip, OutputStream out);

  /**
   * Remove the metadata and file of snip from the storage.
   * This implementation stores the file to a
   * .removed backup file
   *
   * @param snip
   */

  protected void storageRemoveFile(Snip snip, File snipDir) {
    File file = new File(snipDir, getFileName());
    Logger.debug(file + ": exists? " + file.exists());
    if (file.exists()) {
      File backup = new File(file.getPath() + ".removed");
      file.renameTo(backup);
    }
  }

  /**
   * Return the special checker to get the version number
   * from a file.
   *
   * @return
   */
  protected VersionFileNameChecker getVersionFileNameChecker() {
    return new VersionFileNameChecker() {
      public int getVersion(String fileName) {
        return Integer.parseInt(fileName.substring(fileName.lastIndexOf("-")+1));
      }
      public boolean accept(File dir, String name) {
        return name.startsWith(getFileName()) && (name.indexOf('-') != -1);
      }
    };
  }

  /**
   * Load a version of a snip from the file system
   * and the given directory. Version is stored in one file
   *
   * @param snip
   * @param versionDir
   * @return
   */
  protected Map loadVersion(Snip snip, File versionDir, int version) throws IOException {
    if (!versionDir.exists()) {
       return null;
     }

    File versionFile = new File(versionDir, getFileName() + "-" + version);
    if (!versionFile.exists()) {
      return null;
    }

    return loadSnip(new FileInputStream(versionFile));
  }

  /**
   * Store version of snip to the file system and
   * given directory. Version is stored as one file
   *
   * @param snip
   * @param versionDir
   */
  public void storeVersion(Snip snip, File versionDir) {
    if (!versionDir.exists()) {
      versionDir.mkdirs();
    }

    File file = new File(versionDir, getFileName() + "-" + snip.getVersion());
    try {
      storeSnip(snip, new FileOutputStream(file));
    } catch (FileNotFoundException e) {
      Logger.log("FileSnipStorage: unable to store version snip" + snip.getName(), e);
    }
  }

  /**
   * Store a snip to a directory
   *
   * Create two output streams and write
   * the metadata and content to those
   *
   * @param snip
   * @param snipDir
   */
  protected void storeSnip(Snip snip, File snipDir) {
    if (!snipDir.exists()) {
      snipDir.mkdirs();
    }

    File file = new File(snipDir, getFileName());
    if (file.exists()) {
      Logger.log("FileSnipStorage: backing up " + file.getPath());
      File backup = new File(file.getPath() + ".bck");
      file.renameTo(backup);
    }

    try {
      storeSnip(snip, new FileOutputStream(file));
    } catch (FileNotFoundException e) {
      Logger.log("FileSnipStorage: unable to store snip metadata" + snip.getName(), e);
    }
  }

  /**
   * Read all data from the snip file in the given directory to
   * a map
   *
   * @param snipDir Directory with the snip file
   * @return
   * @throws IOException
   */
  protected synchronized Map createSnipFromFile(File snipDir) throws IOException {
    File metadataFile = new File(snipDir, getFileName());
    if (!metadataFile.exists()) {
      return null;
    }

    return loadSnip(new FileInputStream(metadataFile));
  }
}
