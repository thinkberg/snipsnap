<!--
  ** Initial installation ...
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ page pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
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
      <img src="images/snipsnap-logo.png"/>
    </div>
    <table class="configuration">
      <tr>
        <td rowspan="2" class="guide">
          <fmt:message key="install.guide"/><br/>
          <div class="step-info"><fmt:message key="install.guide.${step}"/></div>
        </td>
        <td class="edit">
          <div class="step"><fmt:message key="install.step.${step}"/></div>
          <form class="form" method="post" action="<c:url value='/'/>">
            <c:import url="${step}.jsp"/>
          </form>
        </td>
      </tr>
    </table>
  </body>
</html>
