package com.neotis.config;

import com.neotis.app.Application;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.user.User;
import com.neotis.user.UserManager;

public class PostLog {
  public static void main(String[] args) {
    SnipSpace space = SnipSpace.getInstance();

    Application app = Application.get();

    User user = UserManager.getInstance().load("funzel");
    app.setUser(user);

    Snip snip = space.post("hallo");
  }
}
