<%--
  ** Guide Menu
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr><th colspan="2"><fmt:message key="admin.config.step.administrator"/></th></tr>
  <tr>
    <td><fmt:message key="admin.config.app.admin.login"/></td>
    <td><input type="text" name="app.admin.login" value="<c:out value='${config.adminLogin}' default=""/>"></td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.admin.email"/></td>
    <td><input type="text" name="app.admin.email" value="<c:out value='${config.adminEmail}' default=""/>"></td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.admin.password"/></td>
    <td><input type="text" name="app.admin.password" value=""></td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.admin.password.vrfy"/></td>
    <td><input type="text" name="app.admin.password.vrfy" value=""></td>
  </tr>
</table>
