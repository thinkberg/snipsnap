package com.neotis.snip;

import com.neotis.app.Application;
import com.neotis.user.User;
import com.neotis.util.StringUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Comments {
  private Snip snip;
  private List comments;
  private SnipSpace space;
  private Set users;

  public Comments(Snip snip) {
    this.snip = snip;
    space = SnipSpace.getInstance();
  }

  private void init() {
    if (null == comments) {
      comments = SnipSpace.getInstance().getComments(snip);
    }

    if (null == users) {
      users = new HashSet();
      Iterator iterator = comments.iterator();
      while (iterator.hasNext()) {
        Snip snip = (Snip) iterator.next();
        users.add(snip.getCUser());
      }
    }
  }

  public List getComments() {
    init();
    return comments;
  }

  public Snip postComment(String content, Application app) {
    init();
    String name = "comment-" + snip.getName() + "-" + (getCount() + 1);
    Snip comment = space.create(name, content, app);
    System.err.println(comment);
    comment.setComment(this.snip);
    space.store(comment);
    comments.add(comment);
    users.add(comment.getCUser());
    return comment;
  }

  public String getCommentString() {
    StringBuffer buffer = new StringBuffer();
    StringUtil.plural(buffer, getCount(), "comment");
    if (getCount()>0) {
      buffer.append("(by ");
      appendUserString(buffer);
      buffer.append(")");
    }
    return buffer.toString();
  }

  public void appendUserString(StringBuffer buffer) {
    init();
    Iterator userIterator = users.iterator();
    while (userIterator.hasNext()) {
      String s = (String) userIterator.next();
      User.appendLink(buffer, s);
      if (userIterator.hasNext()) {
        buffer.append(", ");
      }
    }
    return;
  }

  public int getCount() {
    init();
    return comments.size();
  }
}
