<!--
  ** SnipSnap Fatal Error Page
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ page import="java.io.PrintWriter" %>
<%@ page isErrorPage="true" %>
<%@ page pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<% PrintWriter writer = new PrintWriter(out); %>

<% if(null == exception) { response.sendError(HttpServletResponse.SC_FORBIDDEN); %>

<fmt:setBundle basename="i18n.messages" scope="page" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>SnipSnap :: <%= exception.getClass().getName() %></title>
  </head>
  <body>
    <h1>SnipSnap :: <%= exception.getClass().getName() %></h1>
    <%= exception.getLocalizedMessage() %>
    <p>
      <% if (exception.getCause() != null) { %>
           <h3>Caused by <%= exception.getCause().getClass().getName() %></h3>
           <pre><%= exception.getCause().getLocalizedMessage() %></pre>
      <% }  %>
    </p>
    <script language="Javascript" type="text/javascript">
     <!--
      function showHide(obj) {
        if (document.layers) {
          current = (document.layers[obj].display == 'none') ? 'block' : 'none';
          document.layers[obj].display = current;
        } else if (document.all) {
          current = (document.all[obj].style.display == 'none') ? 'block' : 'none';
          document.all[obj].style.display = current;
        } else if (document.getElementById) {
          vista = (document.getElementById(obj).style.display == 'none') ? 'block' : 'none';
          document.getElementById(obj).style.display = vista;
        }
      }
     // -->
     </script>
     <input value="Show Stacktrace" onClick="showHide('stacktrace'); return false;" type="submit">
     <div id="stacktrace" style="display: none">
       <pre><% exception.printStackTrace(writer); %></pre>
     </div>
  </body>
</html>