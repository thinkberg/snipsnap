package com.neotis.config;

import com.neotis.app.Application;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.user.User;
import com.neotis.user.UserManager;

public class Indexer {
  public static void main(String[] args) {
    SnipSpace space = SnipSpace.getInstance();
    space.reIndex();
  }
}
