package org.snipsnap.test.mock;

import java.util.HashMap;
import java.util.Map;

public class MockObject {
  private Map callCount;

  public MockObject() {
    callCount = new HashMap();
  }

  public void clearCalls() {
    callCount = new HashMap();
  }

  protected void inc(String name) {
    if (callCount.containsKey(name)) {
      int count = ((Integer) callCount.get(name)).intValue();
      count++;
      callCount.put(name, new Integer(count));
    } else {
      callCount.put(name, new Integer(1));
    }
  }

  public int getCount(String name) {
     if (callCount.containsKey(name)) {
       return ((Integer) callCount.get(name)).intValue();
     } else {
       return 1;
     }
  }
}
