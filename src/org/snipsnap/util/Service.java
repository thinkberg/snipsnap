package org.snipsnap.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * After the Service class from Sun and the Apache project.
 * With help from FrŽdŽric Miserey.
 *
 * @author Matthias L. Jugel
 * @version $id$
 */
public class Service {

  static HashMap services = new HashMap();

  public static synchronized Iterator providers(Class cls) {
    ClassLoader classLoader = cls.getClassLoader();
    String providerFile = "META-INF/services/" + cls.getName();

    // check whether we already loaded the provider classes
    List providers = (List) services.get(providerFile);
    if (providers != null) {
      return providers.iterator();
    }

    // create new list of providers
    providers = new ArrayList();
    services.put(providerFile, providers);

    try {
      Enumeration providerFiles = classLoader.getResources(providerFile);

      // cycle through the provider files and load classes
      while (providerFiles.hasMoreElements()) {
        try {
          URL url = (URL) providerFiles.nextElement();
          BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

          String line = reader.readLine();
          while (line != null) {
            try {
              // First strip any comment...
              int idx = line.indexOf('#');
              if (idx != -1) {
                line = line.substring(0, idx);
              }

              // Trim whitespace.
              line = line.trim();

              // load class if a line was left
              if (line.length() > 0) {
                // Try and load the class
                //Logger.debug(line);
                Object obj = classLoader.loadClass(line).newInstance();
                // stick it into our vector...
                providers.add(obj);
              }
            } catch (Exception ex) {
              // Just try the next line
            }
            line = reader.readLine();
          }
        } catch (Exception ex) {
          // Just try the next file...
        }
      }
    } catch (IOException ioe) {
      // ignore exception
    }
    return providers.iterator();
  }
}
