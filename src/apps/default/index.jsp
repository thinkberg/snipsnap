<%@ page import="snipsnap.api.app.Application,
                 snipsnap.api.config.Configuration"%>
 <%--
  ** Template for redirection the root page to the start page
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%
  snipsnap.api.config.Configuration snipConfig = snipsnap.api.app.Application.get().getConfiguration();
  response.sendRedirect(snipConfig.getSnipUrl(snipConfig.getStartSnip()));
  return;
%>