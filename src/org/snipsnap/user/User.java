package com.neotis.user;

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
