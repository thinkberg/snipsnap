/*
 * Created by IntelliJ IDEA.
 * User: leo
 * Date: Jul 11, 2002
 * Time: 4:32:15 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.neotis.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

public class JarUtil {
  public static void extract(JarFile file, File target) throws IOException {
    extract(file, target, new PrintWriter(System.err));
  }

  /**
   * Extract jar file and return a map of the checksummed files extracted.
   * @param file the jar file to extract
   * @param target the target directory (will be created if it does not exist)
   * @param out an outputstream for messages
   * @return a map of the checksums
   * @throws IOException
   */
  public static Checksum extract(JarFile file, File target, PrintWriter out) throws IOException {
    if (!target.exists()) {
      target.mkdirs();
    }

    Checksum checksum = new Checksum(target.getAbsolutePath());
    if (target.isDirectory()) {
      byte buf[] = new byte[8192];
      Enumeration entries = file.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = (JarEntry) entries.nextElement();
        File f = new File(target, entry.getName());
        if (entry.isDirectory()) {
          out.println("JarUtil: creating directory '" + f.getName() + "'");
          f.mkdir();
        } else {
          out.println("JarUtil: writing file '" + f.getName() + "'");
          CheckedInputStream fin = new CheckedInputStream(new BufferedInputStream(file.getInputStream(entry)),
                                                          new Adler32());
          BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(f));
          int n = 0;
          while ((n = fin.read(buf)) >= 0) {
            fout.write(buf, 0, n);
          }
          checksum.add(entry.toString(), new Long(fin.getChecksum().getValue()));
          fin.close();
          fout.close();
        }
      }
    }

    return checksum;
  }

  public static Checksum checksumJar(JarFile file, PrintWriter out) throws IOException {
    Checksum checksum = new Checksum(file.toString());
    Enumeration entries = file.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = (JarEntry) entries.nextElement();
      if (entry.isDirectory()) {
        out.println("JarUtil: ignoring directory '" + entry.getName() + "'");
      } else {
        out.print("JarUtil: checksumming file '" + entry.getName() + "': ");
        CheckedInputStream fin = new CheckedInputStream(new BufferedInputStream(file.getInputStream(entry)),
                                                        new Adler32());
        byte buffer[] = new byte[8192];
        while ((fin.read(buffer)) != -1) {
          /* ignore ... */
        }
        Long checkSum = new Long(fin.getChecksum().getValue());
        out.println(Long.toHexString(checkSum.longValue()));
        checksum.add(entry.toString(), checkSum);
        fin.close();
      }
      out.flush();
    }
    return checksum;
  }

  public static void main(String args[]) {
    try {
      JarFile file = new JarFile(args[0]);
      checksumJar(file, new PrintWriter(System.out));
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("JarUtil: usage: JarUtil jarfile");
    }
  }
}
