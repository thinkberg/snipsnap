package com.neotis.snip;

import com.neotis.snip.filter.SnipFormatter;
import com.neotis.user.User;
import com.neotis.util.StringUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.text.SimpleDateFormat;


public class Snip {
  private Snip parent;
  private List children;
  private Snip comment;
  private Comments comments;

  private String name, content;
  private Timestamp cTime, mTime;
  private String cUser, mUser;

  public static String toName(Date date) {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    return sf.format(date);
  }

  public String getNiceTime(Timestamp time) {
    if (time == null) return "";
    java.util.Date now = new java.util.Date();
    int secs = (int) (now.getTime() - time.getTime()) / 1000;
    //int sec = secs % 60;
    int mins = secs / 60;
    int min = mins % 60;
    int hours = mins / 60;
    int hour = hours % 24;
    int days = hours / 24;
    //int years = days / 365;

    StringBuffer nice = new StringBuffer();
    if (mins == 0) {
      nice.append("Just a blink of an eye ago.");
    } else if (hours == 0) {
      StringUtil.plural(nice, min, "minute");
      nice.append(" ago.");
    } else if (days == 0) {
      StringUtil.plural(nice, hour, "hour");
      nice.append(", ");
      StringUtil.plural(nice, min, "minute");
      nice.append(" ago.");
    } else {
      StringUtil.plural(nice, days, "day");
      nice.append(" ago.");
    }
    return nice.toString();
  }

  public String getModified() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Created by");
    User.appendLink(buffer, cUser);
    buffer.append("Last Edited by");
    User.appendLink(buffer, mUser);
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

  public void setCUser(User cUser) {
    this.cUser = cUser.getLogin();
  }

  public void setCUser(String cUser) {
    this.cUser = cUser;
  }

  public String getMUser() {
    return mUser;
  }

  public void setMUser(User mUser) {
    this.mUser = mUser.getLogin();
  }

  public void setMUser(String mUser) {
    this.mUser = mUser;
  }

  public List getChildren() {
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

  public void setComment(Snip comment) {
    this.comment = comment;
  }

  public Snip getComment() {
    return comment;
  }

  public Comments getComments() {
    if (null == comments) {
      comments = new Comments(this);
    }
    return comments;
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
