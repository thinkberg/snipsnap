package com.neotis.snip;

import com.neotis.snip.filter.SnipFormatter;
import com.neotis.user.User;

import java.sql.Timestamp;
import java.util.Collection;


public class Snip {
  private Snip parent;
  private Collection children;

  private String name, content;
  private Timestamp cTime, mTime;
  private User cUser, mUser;

  public Timestamp getCTime() {
    return cTime;
  }

  public String getModified() {
    return "Just a blink of an eye ago.";
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

  public User getCUser() {
    return cUser;
  }

  public void setCUser(User cUser) {
    this.cUser = cUser;
  }

  public User getMUser() {
    return mUser;
  }

  public void setMUser(User mUser) {
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
