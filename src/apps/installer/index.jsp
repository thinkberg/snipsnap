<%@ page import="org.snipsnap.snip.SnipLink"%>
  <!--
  ** Template for redirection the root page to the start page
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<% response.sendRedirect(request.getContextPath() + "/exec/"); return; %>