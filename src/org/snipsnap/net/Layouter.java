package com.neotis.net;

import com.neotis.app.Application;
import com.neotis.user.UserManager;
import com.neotis.user.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Layouter and main handler for web sites.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Layouter extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    HttpSession session = request.getSession(true);
    Application app = (Application)session.getAttribute("app");
    if(app == null) {
      app = new Application();
    }

    User user = app.getUser();
    if(user == null) {
      user = UserManager.getInstance().getUser(request);
    }

    // store user name and app in cookie and session
    response.addCookie(new Cookie("userName", user.getLogin()));
    session.setAttribute("app", app);

    String requestURI = request.getRequestURI();
    if(requestURI.startsWith("/space/")) {

      String path = request.getPathInfo();
      if(null == path) {
        path = "start";
      } else {
        path = path.substring(1);
      }
      request.setAttribute("page", "/snip.jsp?name="+path);
    } else {
      String path = requestURI.substring(6)+".jsp";
      request.setAttribute("page",  path);
    }

    RequestDispatcher dispatcher = request.getRequestDispatcher("/main.jsp");
    dispatcher.forward(request, response);
  }

}
