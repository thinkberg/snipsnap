package org.snipsnap.config;

import org.snipsnap.snip.SnipSpace;

public class Indexer {
  public static void main(String[] args) {
    SnipSpace space = SnipSpace.getInstance();
    space.reIndex();
  }
}
