package org.snipsnap.util;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpc;
import org.radeox.util.logging.Logger;

import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Vector;

public class XmlRpcUtil {
  public static void main(String[] args) {

    try {
      XmlRpc.setEncoding("UTF-8");
      XmlRpcClient xmlrpc = new XmlRpcClient("http://localhost:8668/RPC2");
      Vector params = new Vector();
      for(int n = 1; n < args.length; n++) {
        params.add(args[n]);
      }
      String result = (String) xmlrpc.execute(args[0], params);
      Logger.debug("result=" + result);
    } catch (IOException e) {
      Logger.warn("IOException ", e);
    } catch (XmlRpcException e) {
      Logger.warn("XmlRpcException ", e);
    }
  }
}
