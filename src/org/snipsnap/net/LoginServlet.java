package com.neotis.net;

import com.neotis.app.Application;
import com.neotis.user.User;
import com.neotis.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
  private final static String ERR_PASSWORD = "User name and password do not match!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String login = request.getParameter("login");
    String password = request.getParameter("password");
    String referer = request.getParameter("referer");

    if (request.getParameter("cancel") == null) {
      UserManager um = UserManager.getInstance();
      User user = um.authenticate(login, password);
      if (user == null) {
        response.sendRedirect("/exec/login?login=" + login + "&message=" + ERR_PASSWORD);
        return;
      }
      HttpSession session = request.getSession(true);
      Application app = (Application) session.getAttribute("app");
      if(app == null) {
        app = new Application();
      }
      app.setUser(user);
      // store user name and app in cookie and session
      response.addCookie(new Cookie("userName", user.getLogin()));
      session.setAttribute("app", app);
      response.sendRedirect("/space/" + login);
      return;
    }

    response.sendRedirect(referer != null ? referer : "/space/about");
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String referer = request.getHeader("REFERER");

    if("true".equals(request.getParameter("logoff"))) {
      System.out.println("LoginServlet: Logging user off");
      HttpSession session = request.getSession(true);
      session.invalidate();
      response.addCookie(new Cookie("userName", "Guest"));
    }

    response.sendRedirect(referer != null ? referer : "/space/about");
  }
}
