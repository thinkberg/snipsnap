package com.neotis.net;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Layouter extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    String requestURI = request.getRequestURI();
    if(requestURI.startsWith("/space/")) {

      String path = request.getPathInfo();
      if(null == path) {
        path = "start";
      } else {
        path = path.substring(1);
      }
      System.err.println("Layouter: wiki space request: "+path);
      request.setAttribute("page", "/snip.jsp?name="+path);
    } else {
      String path = requestURI.substring(6)+".jsp";
      System.err.println("Layouter: command request: "+path);
      request.setAttribute("page",  path);
    }

    RequestDispatcher dispatcher = request.getRequestDispatcher("/main.jsp");
    dispatcher.forward(request, response);
  }
}
