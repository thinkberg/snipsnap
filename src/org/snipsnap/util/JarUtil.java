/*
 * Created by IntelliJ IDEA.
 * User: leo
 * Date: Jul 11, 2002
 * Time: 4:32:15 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.snipsnap.util;

import org.radeox.util.logging.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

public class JarUtil {

  public static Checksum extract(JarFile file, File target) throws IOException {
    return extract(file, target, null, null);
  }

  /**
   * Extract jar file and return a map of the checksummed files extracted.
   * @param file the jar file to extract
   * @param target the target directory (will be created if it does not exist)
   * @return a map of the checksums
   * @throws IOException
   */
  public static Checksum extract(JarFile file, File target, List install, List unpack) throws IOException {
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
          f.mkdir();
        } else {
          boolean extract = true, rename = false;
          String name = entry.toString();
          if (install != null && !install.contains(name)) {
            extract = false;
          }
          if (unpack != null && unpack.contains(name)) {
            extract = true;
            rename = true;
          }

          if (extract) {
            System.out.println("Extracting "+f.getAbsolutePath());
            CheckedInputStream fin = new CheckedInputStream(new BufferedInputStream(file.getInputStream(entry)),
                                                            new Adler32());
            if(rename) {
              f = new File(target, entry.getName()+".new");
              System.out.println("Renaming to "+f.getAbsolutePath());
            }
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
    }

    return checksum;
  }

  public static Checksum checksumJar(JarFile file) throws IOException {
    Checksum checksum = new Checksum(file.toString());
    Enumeration entries = file.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = (JarEntry) entries.nextElement();
      if (!entry.isDirectory()) {
        CheckedInputStream fin = new CheckedInputStream(new BufferedInputStream(file.getInputStream(entry)),
                                                        new Adler32());
        byte buffer[] = new byte[8192];
        while ((fin.read(buffer)) != -1) {
          /* ignore ... */
        }
        Long checkSum = new Long(fin.getChecksum().getValue());
        checksum.add(entry.toString(), checkSum);
        fin.close();
      }
    }
    return checksum;
  }

  public static void main(String args[]) {
    try {
      JarFile file = new JarFile(args[0]);
      Checksum checksum = checksumJar(file);
      checksum.store(new File("./CHECKSUMS"));
    } catch (IOException e) {
      Logger.warn("JarUtil: usage: JarUtil jarfile", e);
    }
  }
}
