<!--
  ** Initial installation ...
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%@ page pageEncoding="iso-8859-1" %>
<% response.setContentType("text/html; charset=UTF-8"); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="install.title"/> :: <fmt:message key="install.step.${step}"/></title>
    <link type="text/css" href="default.css" rel="STYLESHEET"/>
  </head>
  <body>
    <div class="header">
      <img src="../images/snipsnap-logo.png"/>
    </div>
    <div class="content">
      <c:import url="${step}.jsp"/>
    </div>
  </body>
</html>


