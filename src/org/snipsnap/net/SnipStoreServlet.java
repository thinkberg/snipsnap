package com.neotis.net;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

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
    if(snip != null) {
      snip.setContent(content);
      space.store(snip);
    } else {
      snip = space.create(name, content);
    }

    response.sendRedirect("/space/"+name);
  }
}
