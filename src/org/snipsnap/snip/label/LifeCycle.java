package org.snipsnap.snip.label;

/**
 * Created by IntelliJ IDEA.
 * User: stephan
 * Date: Sep 4, 2003
 * Time: 10:03:32 AM
 * To change this template use Options | File Templates.
 */
public interface LifeCycle {
  void create();

  void remove();

  void change();
}
