<%--
  ** Registration page template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<div id="snip-wrapper">
 <div id="snip-title"><h1 class="snip-name">Register User</h1></div>
 <%-- display error message --%>
 <c:forEach items="${errors}" var="error">
  <div class="error"><c:out value="${error.value}"/></div>
 </c:forEach>
 <div id="snip-content">
  <form id="form" method="POST" action="../exec/newuser">
   <table>
    <tr <c:if test="${errors['login'] != null}">class="error-position"</c:if>>
     <td><label for="login">User name:</label></td><td><input name="login" type="text" size="20" value="<c:out value="${register['login']}"/>"/></td></tr>
    <tr <c:if test="${errors['email'] != null}">class="error-position"</c:if>>
     <td><label for="email">Email address:</label></td><td><input name="email" type="text" size="20" value="<c:out value="${register['email']}"/>"/></td></tr>
    <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
     <td><label for="password">Password:<label></td><td><input name="password" type="password" size="20" value=""/></td></tr>
    <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
     <td><label for="password2">Password again:<label></td><td><input name="password2" type="password" size="20" value=""/></td></tr>
    <tr><td class="form-buttons">
     <input value="Register" name="register" type="submit"/>
     <input value="Cancel" name="cancel" type="submit"/>
    </td></tr>
   </table>
   <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
  </form>
 </div>
</div>