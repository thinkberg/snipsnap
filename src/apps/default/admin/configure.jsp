<%@ page import="org.snipsnap.config.Configuration"%>
<!--
  ** Initial installation ...
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ page pageEncoding="iso-8859-1"  %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <title><fmt:message key="admin.config.title"/></title>
    <link type="text/css" href="css/config.css" rel="STYLESHEET"/>
  </head>
  <body>
    <div class="header">
      <img src="../images/snipsnap-logo.png"/>
      <c:import url="config/info.jsp"/>
    </div>
    <form action="configure" method="POST">
      <table class="configuration">
        <tr>
          <td rowspan="2" class="guide">
           <c:import url="config/guide.jsp"/>
           <div class="step-info"><fmt:message key="admin.config.guide.${step}" /></div>
          </td>
          <td class="edit">
            <c:import url="config/${step}.jsp"/>
          </td>
        </tr>
        <tr><td class="navigation"><c:import url="config/nav.jsp"/></td></tr>
      </table>
    </form>
  </body>
</html>
