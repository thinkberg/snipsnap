package org.snipsnap.config;

import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;
import java.io.IOException;

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
