/*
 * Created by IntelliJ IDEA.
 * User: leo
 * Date: Dec 22, 2002
 * Time: 3:46:22 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.snipsnap.net.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;

public class EncRequestWrapper extends HttpServletRequestWrapper {
  String encoding = "UTF-8";

  public EncRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  public EncRequestWrapper(HttpServletRequest request, String enc) throws UnsupportedEncodingException {
    super(request);
    "".getBytes(enc);
    encoding = enc;
  }

  private String getEncodedString(String src) {
    if (src != null) {
      try {
        return new String(src.getBytes("iso-8859-1"), encoding);
      } catch (UnsupportedEncodingException e) {
        System.err.println("Error: illegal encoding: " + e);
      }
    }
    return src;
  }

  public String getHeader(String name) {
    return getEncodedString(super.getHeader(name));
  }

  public String getQueryString() {
    return getEncodedString(super.getQueryString());
  }

  public String getRequestURI() {
    return getEncodedString(super.getRequestURI());
  }

  public String getPathInfo() {
    return getEncodedString(super.getPathInfo());
  }

}
