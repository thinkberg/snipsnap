<%--
  ** First Login / Admin
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr>
    <td><fmt:message key="config.app.admin.login.text"/></td>
    <td>
      <fmt:message key="config.app.admin.login"/><br/>
      <input type="text" name="app.admin.login" value="<c:out value='${newconfig.adminLogin}' default=""/>">
      <c:if test="${!empty errors['app.admin.login']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.admin.password.text"/></td>
    <td>
      <fmt:message key="config.app.admin.password"/><br/>
      <input type="password" name="app.admin.password" value="">
      <c:if test="${!empty errors['app.admin.password']}"><img src="images/attention.jpg"></c:if><br/>
      <fmt:message key="config.app.admin.password.vrfy"/><br/>
      <input type="password" name="app.admin.password.vrfy" value="">
      <c:if test="${!empty errors['app.admin.password']}"><img src="images/attention.jpg"></c:if><br/>
      <div class="hint">
        <c:if test="${not empty newconfig.adminPassword}">
          <fmt:message key="config.password.set" />
        </c:if>
      </div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.admin.email.text"/></td>
    <td>
      <fmt:message key="config.app.admin.email"/><br/>
      <input type="text" name="app.admin.email" value="<c:out value='${newconfig.adminEmail}' default=""/>">
      <c:if test="${!empty errors['app.admin.email']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
</table>
