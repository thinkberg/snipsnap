package com.neotis.util;

public class StringUtil {
  public static StringBuffer plural(StringBuffer buffer, int i, String s1, String s2) {
    buffer.append(i);
    buffer.append(" ");
    if (i > 1 || i == 0) {
      buffer.append(s1);
    } else {
      buffer.append(s2);
    }
    return buffer;
  }

  public static StringBuffer plural(StringBuffer buffer, int i, String s) {
    buffer.append(i);
    buffer.append(" ");
    buffer.append(s);
    if (i > 1 || i == 0) {
      buffer.append("s");
    }
    return buffer;
  }
}
