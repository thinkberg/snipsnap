package com.neotis.jsp;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

import javax.servlet.http.HttpServletRequest;

public class SnipBean {
  HttpServletRequest request = null;

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public void setContent(String content) {
    System.err.println("SnipBean: setContent() is not supported, use setRequest()");
  }

  public String getContent() {
    System.err.println("SnipBean: "+request+", "+request.getAttribute("path"));
    String title = (String)request.getAttribute("path");
    Snip snip = SnipSpace.getInstance().load(title);
    return snip.toXML();
  }
}
