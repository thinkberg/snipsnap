package com.neotis.config;

import com.neotis.app.Application;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.user.User;
import com.neotis.user.UserManager;

/**
 * Post an example comment.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class PostComment {
  public static void main(String[] args) {
    SnipSpace space = SnipSpace.getInstance();

    Application app = new Application();
    User user = UserManager.getInstance().load("funzel");
    app.setUser(user);

    Snip snip = space.load("about");
    snip.getComments().postComment("Hahaha, sowas __bloedes__ ist [das]", app);
  }
}
