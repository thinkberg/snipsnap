package com.neotis.snip;

import com.neotis.snip.filter.SnipFormatter;

import java.util.Collection;


public class Snip {
  private String name;
  private String content;
  private Snip parent;
  private Collection children;

  public Collection getChildren() {
    init();
    return children;
  }

  public void addSnip(Snip snip) {
    init();
    if (!children.contains(snip)) {
      children.add(snip);
    }
    snip.setParent(this);
    SnipSpace.getInstance().store(snip);
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

  public Snip getParent() {
    return parent;
  }

  public void setParent(Snip parent) {
    if (parent != this.parent) {
      if (null != this.parent) {
        this.parent.removeSnip(this);
      }
      this.parent = parent;
      parent.removeSnip(this);
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
