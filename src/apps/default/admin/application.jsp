<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:import url="/admin/menu.jsp"/>

<table border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td><b>Application Name:</b></td>
    <td><a href="<c:url value='/'/>"><c:out value="${config.name}"/></a></td>
  </tr>
  <tr>
    <td><b>Administrator:</b></td>
    <td>
     <a href="<c:url value='/space/${config.adminLogin}'/>"><c:out value="${config.adminLogin}"/></a>
     (<a href="<c:out value='mailto:${config.adminEmail}'/>"><c:out value="${config.adminEmail}"/></a>)
    </td>
  </tr>
  <tr>
    <td><b>Host:</b></td>
    <td><c:out value="${config.host}" default="*"/><c:out value=":${config.port}" default=""/></td>
  </tr>
  <tr>
    <td colspan="2">Use the <i>Installer</i> to update the application.</td>
  </tr>
</table>

