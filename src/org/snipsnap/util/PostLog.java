package org.snipsnap.util;

import org.snipsnap.app.Application;
import org.snipsnap.snip.Blog;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.UserManagerFactory;

public class PostLog {
  public static void main(String[] args) {
    Blog blog = SnipSpaceFactory.getInstance().getBlog();

    Application app = Application.get();

    User user = UserManagerFactory.getInstance().load("funzel");
    app.setUser(user);

    Snip snip = blog.post("hallo");
  }
}
