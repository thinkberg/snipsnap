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
import org.snipsnap.versioning.VersionInfo;

import java.io.*;
import java.util.*;
import java.sql.Timestamp;

/**
 * SnipStorage backend that uses files for persisting data. This storage
 * has limitations in the snip name length and possibly characters as well
 * since not all filesystems can store UTF-8 file names.
 *
 * This Storage uses two files for persistance, one for the
 * metadata and one for the content.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public abstract class TwoFileSnipStorage extends FileSnipStorage {

  /**
   * Load the metadata of a snip from an InputStream and
   * store the metadata in a Map.
   *
   * @param in Stream to read from
   * @return
   * @throws IOException
   */
  protected abstract Map loadMetadata(InputStream in) throws IOException;

  /**
   * Load the content of a snip from an InputStream
   *
   * @param in Stream to read from
   * @return
   * @throws IOException
   */

  protected abstract String loadContent(InputStream in) throws IOException;

  /**
   * Return the file name of the metadata file. This should
   * be implemented to return eg. metadata.properties or metadata.xml ...
   *
   * @return
   */
  protected abstract String getMetadataFileName();

  /**
   * Return the file name of the content file. This should
   * be implemented to return eg. content.txt or content.xml ...
   *
   * @return
   */

  protected abstract String getContentFileName();

  /**
   * Store the content of a snip to
   * the output stream. Implementations
   * can store whichever format they choose
   * (plain text, XML, ...)
   *
   * @param snip
   * @param out
   */

  protected abstract void storeContent(Snip snip, OutputStream out);

  /**
   * Store the metadata of a snip to
   * the output stream. Implementations
   * can store whichever format they choose
   * (property, XML, ...)
   *
   * @param snip
   * @param out
   */
  protected abstract void storeMetadata(Snip snip, OutputStream out);

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
        return name.startsWith(getContentFileName()) && (name.indexOf('-') != -1);
      }
    };
  }

   /**
   * Remove the metadata of snip from the storage.
   * This implementation stores the metadata to a
   * .removed backup file
   *
   * @param snip
   */
  protected void storageRemoveMetadata(Snip snip, File snipDir) {
    File metadataFile = new File(snipDir, getMetadataFileName());
    Logger.debug(metadataFile + ": exists? " + metadataFile.exists());
    if (metadataFile.exists()) {
      File backup = new File(metadataFile.getPath() + ".removed");
      metadataFile.renameTo(backup);
    }
  }

  /**
   * Remove the content of snip from the storage.
   * This implementation stores the content to a
   * .removed backup file
   *
   * @param snip
   */
  protected void storageRemoveContent(Snip snip, File snipDir) {
    File contentFile = new File(snipDir, getContentFileName());
    Logger.debug(contentFile+": exists? "+contentFile.exists());
    if (contentFile.exists()) {
      File backup = new File(contentFile.getPath() + ".removed");
      contentFile.renameTo(backup);
    }
  }

  /**
   * Remove the snip from the storage.
   * With a strategy pattern we call this
   * for metadata removal and content removal
   *
   * @param snip Snip to remove
   */
  public void storageRemoveFile(Snip snip, File snipDir) {
    storageRemoveMetadata(snip, snipDir);
    storageRemoveContent(snip, snipDir);
  }

  /**
   * Load a version of a snip from the file system
   * and the given directory
   *
   * @param snip
   * @param versionDir
   * @return
   */
  protected Map loadVersion(Snip snip, File versionDir, int version) throws IOException {
    if (!versionDir.exists()) {
       return null;
     }

    File metadataFile = new File(versionDir, getMetadataFileName() + "-" + version);
    if (!metadataFile.exists()) {
      return null;
    }

    File contentFile = new File(versionDir, getContentFileName() + "-" + version);
    if (!contentFile.exists()) {
      return null;
    }

    FileInputStream metaIn = null;
    FileInputStream contentIn = null;
    Map map = null;
    try {
      metaIn = new FileInputStream(metadataFile);
      contentIn = new FileInputStream(contentFile);
      map = createSnipFromFile(metaIn, contentIn);
    } finally {
      contentIn.close();
      metaIn.close();
    }
    return map;
  }

  /**
   * Store a version of the snip to the
   * file system with the given directory
   *
   * @param snip
   * @param versionDir
   */
  protected void storeVersion(Snip snip, File versionDir) {
    if (!versionDir.exists()) {
      versionDir.mkdirs();
    }

    File metadataFile = new File(versionDir, getMetadataFileName() + "-" + snip.getVersion());
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(metadataFile);
      storeMetadata(snip, out);
    } catch (IOException e) {
      Logger.log("TwoFileSnipStorage: unable to store version snip metadata" + snip.getName(), e);
    } finally {
      close(out);
    }

    File contentFile = new File(versionDir, getContentFileName() + "-" + snip.getVersion());
    try {
      out = new FileOutputStream(contentFile);
      storeContent(snip, out);
    } catch (IOException e) {
      Logger.log("TwoFileSnipStorage: unable to store version snip content" + snip.getName(), e);
    } finally {
      close(out);
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

    File metadataFile = new File(snipDir, getMetadataFileName());
    if (metadataFile.exists()) {
      Logger.log("TwoFileSnipStorage: backing up " + metadataFile.getPath());
      File backup = new File(metadataFile.getPath() + ".bck");
      metadataFile.renameTo(backup);
    }

    FileOutputStream out = null;
    try {
      out = new FileOutputStream(metadataFile);
      storeMetadata(snip, out);
    } catch (IOException e) {
      Logger.log("TwoFileSnipStorage: unable to store snip metadata" + snip.getName(), e);
    } finally {
      close(out);
    }

    File contentFile = new File(snipDir, getContentFileName());
    if (contentFile.exists()) {
      Logger.log("TwoFileSnipStorage: backing up " + contentFile.getPath());
      File backup = new File(contentFile.getPath() + ".bck");
      contentFile.renameTo(backup);
    }

    try {
      out = new FileOutputStream(contentFile);
      storeContent(snip, out);
      out.close();
    } catch (IOException e) {
      Logger.log("TwoFileSnipStorage: unable to store snip content" + snip.getName(), e);
    } finally {
      close(out);
    }
  }

  /**
   * Load all snip data from two files in the given directory
   *
   * @param snipDir
   * @return
   * @throws IOException
   */
  protected synchronized Map createSnipFromFile(File snipDir) throws IOException {
    File metadataFile = new File(snipDir, getMetadataFileName());
    if (!metadataFile.exists()) {
      return null;
    }

    File contentFile = new File(snipDir, getContentFileName());
    if (!contentFile.exists()) {
      return null;
    }

    FileInputStream metaIn = null;
    FileInputStream contentIn = null;
    Map map = null;
    try {
      metaIn = new FileInputStream(metadataFile);
      contentIn = new FileInputStream(contentFile);
      map = createSnipFromFile(metaIn, contentIn);
    } finally {
      contentIn.close();
      metaIn.close();
    }
    return map;
  }

  /**
   * Load all snip data from two input streams
   *
   * @param metadataIn
   * @param contentIn
   * @return
   * @throws IOException
   */
  private Map createSnipFromFile(InputStream metadataIn, InputStream contentIn) throws IOException {
    Map metadata = loadMetadata(metadataIn);
    metadata.put(SnipSerializer.SNIP_CONTENT, loadContent(contentIn));
    return metadata;
  }
}
