package com.neotis.net;

import com.neotis.app.Application;
import com.neotis.user.User;
import com.neotis.user.UserManager;
import com.neotis.snip.SnipSpace;
import com.neotis.snip.HomePage;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

public class NewUserServlet extends HttpServlet {
  private final static String ERR_EXISTS = "User exists, please user another login name!";
  private final static String ERR_PASSWORD = "Password does not match!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String login = request.getParameter("login");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String password2 = request.getParameter("password2");

    if (request.getParameter("cancel") == null) {
      UserManager um = UserManager.getInstance();
      User user = um.load(login);
      // check whether user exists or not
      if (user != null) {
        response.sendRedirect("/exec/register?login=" + login + "&email=" + email + "&message=" + ERR_EXISTS);
        return;
      }
      // check whether the password is correctly typed
      if (!password.equals(password2)) {
        response.sendRedirect("/exec/register?login=" + login + "&email=" + email + "&message=" + ERR_PASSWORD);
        return;
      }
      user = um.create(login, password);

      HttpSession session = request.getSession(true);
      Application app = (Application) session.getAttribute("app");
      app.setUser(user);
      HomePage.create(login, app);
      // store user name and app in cookie and session
      response.addCookie(new Cookie("userName", user.getLogin()));
      session.setAttribute("app", app);
      response.sendRedirect("/space/"+login);
      return;
    }

    String referer = request.getParameter("referer");
    response.sendRedirect(referer != null ? referer : "/space/about");
  }
}
