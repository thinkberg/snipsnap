<!--
  ** Admin main page.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>SnipSnap :: Admin :: <c:out value="${config.name}"/></title>
    <link type="text/css" href="<c:url value="/admin/default.css"/>" rel="STYLESHEET"/>
  </head>
  <body>
    <table width="800" border="0" cellpadding="0" cellspacing="8">
      <tr>
        <td colspan="3" valign="top" width="100%"><s:image name="snip"/></td>
      </tr>
      <td colspan="3" valign="top"><h1><a href="<c:url value='/'/>"><c:out value="${config.name}"/></a></h1></td>
      <tr>
        <td valign="top" class="menu">
          <c:import url="/admin/menu.jsp"/>
        </td>
        <td width="1" style="border-right: 1px solid black;">&nbsp;</td>
        <td width="100%" valign="top">
          <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td valign="top" width="100%">
                 <c:choose>
                   <c:when test="${admin.admin}">
                     <c:import url="/admin${page}"/>
                   </c:when>
                   <c:otherwise>
                     You are not the administrator. Please <a href="<c:url value='/'/>">go back to <c:out value="${config.name}"/></a>!
                   </c:otherwise>
                 </c:choose>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr><td colspan="3">
        <p align="center">
          <a href="http://www.snipsnap.org/">www.snipsnap.org</a> | Copyright 2000-2002 Matthias L. Jugel, Stephan J.Schmidt<br/>
          <a href="http://www.snipsnap.org/"><s:image name="logo_small"/></a>
        </p>
      </td></tr>
    </table>
  </body>
</html>


