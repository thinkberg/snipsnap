package org.snipsnap.config;

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

public class PostLog {
  public static void main(String[] args) {
    SnipSpace space = SnipSpace.getInstance();

    Application app = Application.get();

    User user = UserManager.getInstance().load("funzel");
    app.setUser(user);

    Snip snip = space.post("hallo");
  }
}
