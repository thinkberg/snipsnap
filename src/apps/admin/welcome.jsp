<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ page import="com.neotis.server.AppServer,
                 org.mortbay.jetty.Server,
                 org.mortbay.http.HttpContext"%>
<%
  Server server = AppServer.getServer();
  HttpContext context[] = server.getContexts();
%>

<h3>Willkommen bei SnipSnap</h3>

<% if(null == context) { %>
  <b>The application is not yet configured.</b>
<% } else { %>
  <b>The application is configured:
     <% for(int i = 0; i < context.length; i++) { %>
       <%= context[i] %>
     <% } %>
  </b>
<% } %>