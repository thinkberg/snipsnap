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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExtractor {
  public static void extract(JarFile file, File target) throws IOException {
    extract(file, target, new PrintWriter(System.err));
  }

  public static void extract(JarFile file, File target, PrintWriter out) throws IOException {
    if (!target.exists()) {
      target.mkdirs();
    }

    if (target.isDirectory()) {
      byte buf[] = new byte[8192];
      Enumeration entries = file.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = (JarEntry) entries.nextElement();
        File f = new File(target, entry.getName());
        if (entry.isDirectory()) {
          out.println("JarExtractor: creating directory '"+f.getName()+"'");
          f.mkdir();
        } else {
          out.println("JarExtractor: writing file '"+f.getName()+"'");
          BufferedInputStream fin = new BufferedInputStream(file.getInputStream(entry));
          BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(f));
          int n = 0;
          while ((n = fin.read(buf)) >= 0) {
            fout.write(buf, 0, n);
          }
          fin.close();
          fout.close();
        }
      }
    }
  }
}
