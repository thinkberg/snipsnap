package com.neotis.jsp;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

import javax.servlet.http.HttpServletRequest;

public class SnipBean {
  Snip snip = null;

  HttpServletRequest request = null;
  String name, content;

  public void setName(String name) {
    System.err.println("setName(" + name + ")");
    this.name = name;
    snip = SnipSpace.getInstance().load(name);
  }

  public String getName() {
    if (snip != null) {
      return snip.getName();
    } else {
      return name != null ? name : "";
    }
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    if (snip != null) {
      return snip.getContent();
    }
    return content != null ? content : "";
  }

  public String getXMLContent() {
    System.err.println("getXMLContent()");
    if (snip != null) {
      return snip.toXML();
    }
    return "";
  }

}
