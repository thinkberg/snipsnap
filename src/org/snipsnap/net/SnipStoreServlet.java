package com.neotis.net;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.user.UserManager;
import com.neotis.user.User;
import com.neotis.app.Application;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

public class SnipStoreServlet extends HttpServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String name = request.getParameter("name");
    String content = request.getParameter("content");
    SnipSpace space = SnipSpace.getInstance();
    Snip snip = space.load(name);

		Application app = new Application();
		User user = UserManager.getInstance().load("funzel");
		app.setUser(user);
		
    if(snip != null) {
      snip.setContent(content);
      space.store(snip, app);
    } else {
      snip = space.create(name, content, app);
    }

    response.sendRedirect("/space/"+name);
  }
}
