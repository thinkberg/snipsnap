<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Permission settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr><th colspan="2"><fmt:message key="config.step.permissions"/></th></tr>
  <tr>
    <td><fmt:message key="config.app.perm.register.text"/></td>
    <td>
      <fmt:message key="config.app.perm.register"/><br/>
      <input type="checkbox" name="app.perm.register"
        <c:if test="${config.permRegister == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.perm.weblogsPing.text"/></td>
    <td>
      <fmt:message key="config.app.perm.weblogsPing"/><br/>
      <input type="checkbox" name="app.perm.weblogsPing"
        <c:if test="${config.permWeblogsPing == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
    <tr>
    <td><fmt:message key="config.app.perm.notification.text"/></td>
    <td>
      <fmt:message key="config.app.perm.notification"/><br/>
      <input type="checkbox" name="app.perm.notification"
        <c:if test="${config.permNotification == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.perm.externalImages.text"/></td>
    <td>
      <fmt:message key="config.app.perm.externalImages"/><br/>
      <input type="checkbox" name="app.perm.externalImages"
        <c:if test="${config.permExternalImages == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
</table>
