<%@ page import="snipsnap.api.config.Configuration"%>
 <%--
  ** Initial installation ...
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr>
    <td><fmt:message key="install.app.host.text"/></td>
    <td>
      <fmt:message key="install.app.host"/><br/>
      <input type="text" name="app.host" size="40" value="<c:out value='${config["app.host"]}'/>">
      <c:if test="${!empty errors['app.host']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="install.app.port.text"/></td>
    <td>
      <fmt:message key="install.app.port"/><br/>
      <input type="text" name="app.port" size="40" value="<c:out value='${config["app.port"]}'/>">
      <c:if test="${!empty errors['app.port']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="install.app.path.text"/></td>
    <td>
      <fmt:message key="install.app.path"/><br/>
      <input type="text" name="app.path" size="40" value="<c:out value='${config["app.path"]}'/>">
      <c:if test="${!empty errors['app.path']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
</table>
