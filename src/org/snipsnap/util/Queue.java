package com.neotis.util;

import com.neotis.snip.Snip;

import java.util.LinkedList;
import java.util.List;

public class Queue {
  private LinkedList queue;
  private int size;

  public Queue(int size) {
    this.size = size;
    queue = new LinkedList();
  }

  public void fill(List list) {
    queue.clear();
    queue.addAll(list);
  }

  public Snip add(Snip snip) {
    if (queue.contains(snip)) {
      queue.remove(snip);
    }

    if (queue.size() == size) {
      queue.removeLast();
    }
    queue.addFirst(snip);
    return snip;
  }

  public void remove(Snip snip) {
    queue.remove(snip);
  }

  public List get() {
    return (List) queue;
  }
}
