<%--
  ** Registration page template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name"><fmt:message key="login.register.title"/></h1></div>
 <%-- display error message --%>
 <c:forEach items="${errors}" var="error">
  <div class="error"><fmt:message key="${error.value}"/></div>
 </c:forEach>
 <div class="snip-content">
  <form class="form" method="post" action="exec/newuser">
   <table>
    <tr <c:if test="${errors['login'] != null}">class="error-position"</c:if>>
     <td><label for="login"><fmt:message key="login.user.name"/></label></td><td><input id="login" name="login" type="text" size="20" value="<c:out value="${param['login']}"/>"/></td></tr>
    <tr <c:if test="${errors['email'] != null}">class="error-position"</c:if>>
     <td><label for="email"><fmt:message key="login.user.email"/></label></td><td><input id="email" name="email" type="text" size="20" value="<c:out value="${param['email']}"/>"/></td></tr>
    <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
     <td><label for="password"><fmt:message key="login.password"/><label></td><td><input id="password" name="password" type="password" size="20" value=""/></td></tr>
    <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
     <td><label for="password2"><fmt:message key="login.password.again"/><label></td><td><input id="password2" name="password2" type="password" size="20" value=""/></td></tr>
    <tr><td class="form-buttons" colspan="2">
     <input value="<fmt:message key="login.register"/>" name="register" type="submit"/>
     <input value="<fmt:message key="dialog.cancel"/>" name="cancel" type="submit"/>
    </td></tr>
   </table>
   <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
  </form>
 </div>
</div>