<%@ page import="java.util.Set"%>
<%--
  ** User management: edit/create user
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr>
    <td><fmt:message key="config.users.login.text"/></td>
    <td>
      <fmt:message key="config.users.login"/><br/>
      <c:choose>
        <c:when test="${not empty create}">
          <input type="text" name="config.users.login" value="<c:out value='${editUser.login}' default=""/>">
        </c:when>
        <c:otherwise>
          <input type="hidden" name="config.users.login" value="<c:out value='${editUser.login}' default=""/>">
          <input type="text" name="disabled" value="<c:out value='${editUser.login}' default=""/>" disabled="disabled">
        </c:otherwise>
      </c:choose>
      <c:if test="${!empty errors['users.login']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.users.password.text"/></td>
    <td>
      <fmt:message key="config.users.password"/><br/>
      <input type="password" name="config.users.password" value="">
      <c:if test="${!empty errors['users.password']}"><img src="images/attention.jpg"></c:if><br/>
      <fmt:message key="config.users.password.vrfy"/><br/>
      <input type="password" name="config.users.password.vrfy" value="">
      <c:if test="${!empty errors['users.password']}"><img src="images/attention.jpg"></c:if><br/>
      <div class="hint">
        <c:if test="${not empty editUser.passwd}">
          <fmt:message key="config.password.set" />
        </c:if>
      </div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.users.email.text"/></td>
    <td>
      <fmt:message key="config.users.email"/><br/>
      <input type="text" name="config.users.email" value="<c:out value='${editUser.email}' default=""/>">
      <c:if test="${!empty errors['users.email']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.users.roles.text"/></td>
    <td>
      <fmt:message key="config.users.roles"/><br/>
       <c:set var="userRoles" value="${editUser.roles.roleSet}"/>
       <% Set userRoles = (Set) pageContext.findAttribute("userRoles"); %>
       <c:forEach items="${editUser.roles.allRoles}" var="role">
         <input type="checkbox" name="config.users.roles" value="<c:out value='${role}'/>" <%= userRoles.contains(pageContext.findAttribute("role")) ? "checked=\"checked\"" : "" %>/>
         <c:out value="${role}"/><br/>
       </c:forEach>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.users.status.text"/></td>
    <td>
      <fmt:message key="config.users.status"/><br/>
      <input name="config.users.status" type="text" size="20" value="<c:out value='${editUser.status}'/>"/>
    </td>
  <tr>
    <td></td>
    <td>
      <c:choose>
        <c:when test="${empty create}">
          <input type="submit" name="save" value="<fmt:message key="config.users.save"/>">
        </c:when>
        <c:otherwise>
          <input type="submit" name="create" value="<fmt:message key="config.users.create"/>">
        </c:otherwise>
      </c:choose>
      <input type="submit" name="cancel" value="<fmt:message key="config.users.cancel"/>">
    </td>
  </tr>
</table>
