package org.snipsnap.util;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.util.Vector;

public class XmlRpcUtil {
  public static void main(String[] args) {

    try {
      XmlRpcClient xmlrpc = new XmlRpcClient("http://localhost:8668/RPC2");
      Vector params = new Vector();
      for(int n = 1; n < args.length; n++) {
        params.add(args[n]);
      }
      String result = (String) xmlrpc.execute(args[0], params);
      System.out.println("result=" + result);
    } catch (IOException e) {
      System.err.println("IOException "+e);
      e.printStackTrace();
    } catch (XmlRpcException e) {
      System.err.println("XmlRpcException "+ e);
      e.printStackTrace();
    }
  }
}
