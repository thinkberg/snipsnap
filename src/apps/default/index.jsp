<%@ page import="org.snipsnap.app.Application,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Template for redirection the root page to the start page
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%
  Configuration snipConfig = Application.get().getConfiguration();
  response.sendRedirect(snipConfig.getSnipUrl(snipConfig.getStartSnip()));
  return;
%>