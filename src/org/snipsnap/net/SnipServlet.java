package com.neotis.net;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SnipServlet extends HttpServlet {
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    System.err.println("SnipServlet: ");
    String title = request.getPathInfo().substring(1);
    System.err.println("SnipServlet: looking up: "+title);
    Snip snip = SnipSpace.getInstance().load(title);
    System.err.println("SnipServlet: content: "+snip.toXML());
    request.setAttribute("content", snip.toXML());
    request.getRequestDispatcher("/snip.jsp").forward(request, response);
  }
}
