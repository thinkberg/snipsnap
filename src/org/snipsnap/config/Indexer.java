package org.snipsnap.config;

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

public class Indexer {
  public static void main(String[] args) {
    SnipSpace space = SnipSpace.getInstance();
    space.reIndex();
  }
}
