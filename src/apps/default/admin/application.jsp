<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<table border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td>Application Name: </td>
    <td><a href="<c:url value='/'/>"><c:out value="${config.name}"/></a></td>
  </tr>
  <tr>
    <td>Administrator: </td>
    <td>
     <a href="<c:url value='/space/${config.adminLogin}'/>"><c:out value="${config.adminLogin}"/></a>
     (<a href="<c:out value='mailto:${config.adminEmail}'/>"><c:out value="${config.adminEmail}"/></a>)
    </td>
  </tr>
  <tr>
    <td>Host: </td>
    <td><c:out value="${config.host}" default="*"/><c:out value=":${config.port}" default=""/></td>
  </tr>
</table>

