package com.neotis.user;

import javax.servlet.http.HttpSession;

public class UserManager {
  private static UserManager instance;

  public static synchronized UserManager getInstance() {
    if (null == instance) {
      instance = new UserManager();
    }
    return instance;
  }

  public User getUser(HttpSession session) {
    return new User();
  }

  public User authenticate(String login, String password) {
    return new User();

  }

  public User create(String login, String password) {
    return new User();

  }

  public User store(User user) {
    return new User();

  }

  public void remove(User user) {

  }

  public User load(String login) {
    return new User();

  }

}
