<%@ page import="org.snipsnap.user.UserManager,
                 org.snipsnap.container.Components,
                 java.util.List,
                 java.util.Set"
 %>
<%--
  ** User management: list existing users
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
      <input type="text" name="config.users.login" value="<c:out value='${user.login}' default=""/>"
       <c:if test="${empty user}">disabled="disabled"</c:if>>
      <c:if test="${!empty errors['config.users.login']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.users.password.text"/></td>
    <td>
      <fmt:message key="config.users.password"/><br/>
      <input type="password" name="config.users.password" value="">
      <c:if test="${!empty errors['config.users.password']}"><img src="images/attention.jpg"></c:if><br/>
      <fmt:message key="config.users.password.vrfy"/><br/>
      <input type="password" name="config.users.password.vrfy" value="">
      <c:if test="${!empty errors['config.users.password']}"><img src="images/attention.jpg"></c:if><br/>
      <div class="hint">
        <c:if test="${not empty user.passwd}">
          <fmt:message key="config.password.set" />
        </c:if>
      </div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.users.email.text"/></td>
    <td>
      <fmt:message key="config.users.email"/><br/>
      <input type="text" name="config.users.email" value="<c:out value='${user.email}' default=""/>">
      <c:if test="${!empty errors['config.users.email']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.users.roles.text"/></td>
    <td>
      <fmt:message key="config.users.roles"/><br/>
       <c:set var="userRoles" value="${user.roles.roleSet}"/>
       <% Set userRoles = (Set) pageContext.findAttribute("userRoles"); %>
       <c:forEach items="${user.roles.allRoles}" var="role">
         <input type="checkbox" name="config.users.roles" value="<c:out value='${role}'/>" <%= userRoles.contains(pageContext.findAttribute("role")) ? "checked=\"checked\"" : "" %>/>
         <c:out value="${role}"/><br/>
       </c:forEach>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.users.status.text"/></td>
    <td>
      <fmt:message key="config.users.status"/><br/>
      <input name="status" type="text" size="20" value="<c:out value='${user.status}'/>"/>
    </td>
  <tr>
    <td></td>
    <td>
      <input type="submit" name="save" value="<fmt:message key="config.users.save"/>">
      <input type="submit" name="cancel" value="<fmt:message key="config.users.cancel"/>">
    </td>
  </tr>
</table>
