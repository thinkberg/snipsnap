package org.snipsnap.config;

import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceFactory;

public class Indexer {
  public static void main(String[] args) {
    SnipSpace space = SnipSpaceFactory.getInstance();
    space.reIndex();
  }
}
