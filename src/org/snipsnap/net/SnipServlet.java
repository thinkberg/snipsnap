package com.neotis.net;

import com.neotis.snip.SnipSpace;
import com.neotis.snip.Snip;
import com.neotis.snip.filter.SnipFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import java.io.PrintWriter;
import java.io.IOException;

public class SnipServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

    res.setContentType("text/html");
    PrintWriter out = res.getWriter();

    out.println("<html><body>");
    Snip snip = SnipSpace.getInstance().load("about");
    out.println(SnipFormatter.toXML(snip.getContent()));
    out.println("</body></html>");

    out.close();
  }
}
