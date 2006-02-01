package org.snipsnap.util;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpc;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Vector;
import java.util.Hashtable;

public class XmlRpcUtil {
  public static void main(String[] args) {

    try {
      XmlRpcClient xmlrpc = new XmlRpcClient("http://localhost:8668/RPC2");
      xmlrpc.setBasicAuthentication("leo", "leo");

      Vector params = new Vector();
//      for(int n = 1; n < args.length; n++) {
//        params.add(args[n]);
//      }

      params.add("SuperCalc/Speichern von Logdatein/Schaltfläche Speichern von Logdatein");

      Object result = (Object) xmlrpc.execute("AuthEudibamus.getModelElemByName", params);
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
