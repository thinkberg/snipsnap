package org.snipsnap.config;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.util.Vector;

public class XmlRpcPost {
  public static void main(String[] args) {

    try {
      XmlRpcClient xmlrpc = new XmlRpcClient("http://localhost:8668/RPC2");
      Vector params = new Vector();
      params.add("");
      params.add("");
      params.add("stephan");
      params.add("stephan");
      params.add("boing");
      params.add(new Boolean(true));
      String result = (String) xmlrpc.execute("blogger.newPost", params);
      System.out.println("result=" + result);
    } catch (IOException e) {
      System.err.println("IOException " + e.getMessage());
    } catch (XmlRpcException e) {
      System.err.println("XmlRpcException " + e.getMessage());
    }
  }
}
