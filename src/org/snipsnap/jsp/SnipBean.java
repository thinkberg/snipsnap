package com.neotis.jsp;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

public class SnipBean {
  Snip snip = null;

  HttpServletRequest request = null;
  String name, content;

  public String getModified() {
    return snip.getModified();
  }

  public String getComments() {
    return snip.getComments().getCommentString();
  }

  public Timestamp getCTime() {
    return snip.getCTime();
  }

  public Timestamp getMTime() {
    return snip.getMTime();
  }

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
