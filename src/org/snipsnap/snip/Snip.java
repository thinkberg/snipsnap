package com.neotis.snip;

import com.neotis.snip.filter.SnipFormatter;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.Collection;


public class Snip {
  private Snip parent;
  private Collection children;

  private String name, content;
  private Timestamp cTime, mTime;
  private String cUser, mUser;

  public String getNiceTime(Timestamp time) {
    if(time == null) return "";
    java.util.Date now = new java.util.Date();
    int secs = (int) (now.getTime() - time.getTime()) /1000;
    //int sec = secs % 60;
    int mins = secs / 60;
    int min = mins % 60;
    int hours = mins / 60;
    int hour = hours % 24;
    int days = hours / 24;
    //int years = days / 365;

    String nice = "";
    if (mins==0) {
      nice = "Just a blink of an eye ago.";
    } else if (hours==0) {
      nice = min + " minutes ago.";
    } else if (days==0) {
      nice = hour + " hours, " + min +  " minutes ago.";
    } else {
      nice = days + " days ago.";
    }
    return nice;
  }

  public String getModified() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Created by <a href=\"/space/");
    buffer.append(cUser);
    buffer.append("\">");
    buffer.append(cUser);
    buffer.append("</a> - Last Edited by <a href=\"/space/");
    buffer.append(mUser);
    buffer.append("\">");
    buffer.append(mUser);
    buffer.append("</a> ");
    buffer.append(getNiceTime(mTime));
    return buffer.toString();
  }

  public Timestamp getCTime() {
    return cTime;
  }

  public void setCTime(Timestamp cTime) {
    this.cTime = cTime;
  }

  public Timestamp getMTime() {
    return mTime;
  }

  public void setMTime(Timestamp mTime) {
    this.mTime = mTime;
  }

  public String getCUser() {
    return cUser;
  }

  public void setCUser(String cUser) {
    this.cUser = cUser;
  }

  public String getMUser() {
    return mUser;
  }

  public void setMUser(String mUser) {
    this.mUser = mUser;
  }

  public Collection getChildren() {
    init();
    return children;
  }

  public void addSnip(Snip snip) {
    init();
    snip.setParent(this);
    if (!children.contains(snip)) {
      children.add(snip);
    }
    SnipSpace.getInstance().store(snip);
  }

  public Snip getParent() {
    return parent;
  }

  public void setParent(Snip parentSnip) {
    if (parentSnip != this.parent) {
      if (null != this.parent) {
        this.parent.removeSnip(this);
      }
      this.parent = parentSnip;
    }
  }

  public void removeSnip(Snip snip) {
    init();
    if (children.contains(snip)) {
      children.remove(snip);
      // snip.setParent(null);
    }
  }

  private void init() {
    if (null == children) {
      children = SnipSpace.getInstance().getChildren(this);
    }
  }


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
