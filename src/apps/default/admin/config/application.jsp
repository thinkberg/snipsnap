<%@ page import="org.snipsnap.config.Configuration"%>
 <%--
  ** Guide Menu
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr><th colspan="2"><fmt:message key="admin.config.step.application"/></th></tr>
  <tr>
    <td><fmt:message key="admin.config.app.name.txt"/></td>
    <td>
      <fmt:message key="admin.config.app.name"/><br/>
      <input type="text" name="app.name" value="<c:out value='${config.name}' default="My SnipSnap"/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.tagline.txt"/></td>
    <td><input type="text" name="app.tagline" value="<c:out value='${config.tagline}' default="Where I keep my Knowledge"/>"></td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.logo.txt"/></td>
    <td><input type="file" name="app.logo" value="<c:out value='${config.logo}'/>" accept="image/*"></td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.perm.register.txt"/></td>
    <td>
      <input type="checkbox" name="app.perm.register"
        <c:if test="${config.permRegister == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.perm.notification"/></td>
    <td>
      <input type="checkbox" name="app.perm.notification"
        <c:if test="${config.permNotification == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.perm.weblogsPing"/></td>
    <td>
      <input type="checkbox" name="app.perm.weblogsPing"
        <c:if test="${config.permWeblogsPing == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.perm.externalImages"/></td>
    <td>
      <input type="checkbox" name="app.perm.externalImages"
        <c:if test="${config.permExternalImages == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>

</table>
