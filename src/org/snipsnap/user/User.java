package com.neotis.user;

/**
 * User class.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class User {

  private String login;
  private String passwd;

  public User(String login, String passwd) {
    this.login = login;
    this.passwd = passwd;
  }

  public String getPasswd() {
    return passwd;
  }

  public String getLogin() {
    return login;
  }
}
