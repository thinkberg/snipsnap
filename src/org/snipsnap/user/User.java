package com.neotis.user;

public class User {

  private String login;
  private String passwd;

  public static void appendLink(StringBuffer buffer, String name) {
    buffer.append(" <a href=\"/space/");
    buffer.append(name);
    buffer.append("\">");
    buffer.append(name);
    buffer.append("</a> ");
  }

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
