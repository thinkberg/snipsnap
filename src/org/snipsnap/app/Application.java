package com.neotis.app;

import com.neotis.user.User;

/**
 * The application object contains information about current users and other
 * session specific information.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Application {
  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
    return;
  }
}
