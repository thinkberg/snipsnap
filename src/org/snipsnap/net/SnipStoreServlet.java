package com.neotis.net;

import com.neotis.app.Application;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SnipStoreServlet extends HttpServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String name = request.getParameter("name");
    SnipSpace space = SnipSpace.getInstance();
    Snip snip = space.load(name);

    if (request.getAttribute("cancel") == null) {
      String content = request.getParameter("content");

      HttpSession session = request.getSession();
      Application app = null;
      if (session != null) {
        app = (Application) session.getAttribute("app");
      } else {
        app = new Application();
        app.setUser(UserManager.getInstance().getUser(null));
      }

      if (snip != null) {
        snip.setContent(content);
        space.store(snip, app);
      } else {
        snip = space.create(name, content, app);
      }
    } else if(snip == null) {
      // return to referrer if the snip cannot be found
      response.sendRedirect(request.getParameter("referer"));
      return;
    }

    response.sendRedirect("/space/" + name);
  }
}
