package org.snipsnap.util;

import snipsnap.api.app.Application;
import org.snipsnap.snip.Blog;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpaceFactory;
import snipsnap.api.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.UserManagerFactory;

public class PostLog {
  public static void main(String[] args) {
    Blog blog = SnipSpaceFactory.getInstance().getBlog();

    Application app = Application.get();

    snipsnap.api.user.User user = UserManagerFactory.getInstance().load("funzel");
    app.setUser(user);

    Snip snip = blog.post("hallo");
  }
}
