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
package com.neotis.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class FileUtil {

  public static Checksum checksumDirectory(File file) throws IOException {
    Checksum checksum = new Checksum(file.getAbsolutePath());
    if (file.isDirectory()) {
      checksumFiles(file, file.getPath(), checksum);
    } else {
      checksumFile(file, file.getPath(), checksum);
    }
    return checksum;
  }


  private static void checksumFiles(File file, String root, Checksum checksum) throws IOException {
    File files[] = file.listFiles();
    for (int i = 0; i < files.length; i++) {
      if (files[i].isDirectory()) {
        checksumFiles(files[i], root, checksum);
      } else {
        checksumFile(file, root, checksum);
      }
    }
  }

  private static void checksumFile(File file, String root, Checksum checksum) throws IOException {
    CheckedInputStream fin = new CheckedInputStream(new BufferedInputStream(new FileInputStream(file)),
                                                    new Adler32());
    byte buffer[] = new byte[8192];
    while ((fin.read(buffer)) != -1) {
      /* ignore ... */
    }
    Long checkSum = new Long(fin.getChecksum().getValue());
    checksum.add(file.getAbsolutePath(), checkSum);
    fin.close();
  }
}
