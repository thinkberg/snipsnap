package com.neotis.snip;

import com.neotis.app.Application;

/**
 * Static class to create a home-page snip.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class HomePage {
  public static void create(String login, Application app) {
    Snip snip = null;
    String hp = "\n{snips-by-user:"+login+"}";
    SnipSpace space = SnipSpace.getInstance();
    if (space.exists(login)) {
      snip = space.load(login);
      snip.setContent(snip.getContent() +
        hp );
      space.store(snip);
    } else {
      snip = space.create(login, hp, app);
    }
    return;
  }
}