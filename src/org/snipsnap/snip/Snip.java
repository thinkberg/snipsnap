package com.neotis.snip;

import com.neotis.snip.filter.SnipFormatter;


public class Snip {
  private String name;
  private String content;

  public Snip(String name, String content) {
    this.name = name;
    this.content = content;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String toXML() {
    return SnipFormatter.toXML(this, getContent());
  }
}
