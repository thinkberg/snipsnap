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
    String path = request.getPathInfo();
    if(null == path) {
      path = "start";
    } else {
      path = path.substring(1);
    }
    request.setAttribute("page", "/snip.jsp");
    request.setAttribute("path", path);

    RequestDispatcher dispatcher = request.getRequestDispatcher("/main.jsp");
    dispatcher.forward(request, response);
  }
}
