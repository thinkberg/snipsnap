package org.snipsnap.util;

import java.util.BitSet;
import java.util.StringTokenizer;

public class URLEncoderDecoder {

  static BitSet dontNeedEncoding;

  static {
    dontNeedEncoding = new BitSet(256);
    int i;
    for (i = 'a'; i <= 'z'; i++) {
      dontNeedEncoding.set(i);
    }
    for (i = 'A'; i <= 'Z'; i++) {
      dontNeedEncoding.set(i);
    }
    for (i = '0'; i <= '9'; i++) {
      dontNeedEncoding.set(i);
    }
    dontNeedEncoding.set(' '); /* encoding a space to a + is done in the encode() method */
    dontNeedEncoding.set('-');
    dontNeedEncoding.set('_');
    dontNeedEncoding.set('.');
    dontNeedEncoding.set('*');
  }


  public static String encode(String s) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      int c = (int) s.charAt(i);
      if (dontNeedEncoding.get(c)) {
        if (c == ' ') {
          result.append('+');
        } else {
          result.append((char) c);
        }
      } else {
        result.append('%').append(Integer.toHexString(c & 0xFF).toUpperCase());
      }
    }
    return result.toString();
  }

  public static String decode(String s) {
    StringBuffer result = new StringBuffer();
    for(int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if(c == '+') {
        result.append(' ');
      } else if(c == '%') {
        result.append((char)Integer.parseInt(s.substring(i+1, i+3), 16));
        i+= 2;
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }
}
