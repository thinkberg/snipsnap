<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Expert Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr><th colspan="2"><fmt:message key="admin.config.step.proxy"/></th></tr>
  <tr>
    <td><fmt:message key="admin.config.app.real.autodetect.text"/></td>
    <td>
      <fmt:message key="admin.config.app.real.autodetect"/><br/>
      <input type="checkbox" name="app.real.autodetect" <c:if test="${config.realAutodetect == 'true'}">checked=checked</c:if>>
      <div class="hint">(<fmt:message key="admin.config.detected"/>: <c:out value="${config.url}"/>)</div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.real.host.text"/></td>
    <td>
      <fmt:message key="admin.config.app.real.host"/><br/>
      <input type="text" name="app.real.host" size="40" value="<c:out value='${config.properties["app.real.host"]}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.real.port.text"/></td>
    <td>
      <fmt:message key="admin.config.app.real.port"/><br/>
      <input type="text" name="app.real.port" size="40" value="<c:out value='${config.properties["app.real.port"]}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.real.path.text"/></td>
    <td>
      <fmt:message key="admin.config.app.real.path"/><br/>
      <input type="text" name="app.real.path" size="40" value="<c:out value='${config.properties["app.real.path"]}'/>">
    </td>
  </tr>
</table>
