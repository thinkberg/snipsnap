<!--
  ** Registration page template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">Register User</h1>

<!-- display error message -->
<c:forEach items="${errors}" var="value">
  <div class="error"><c:out value="${value}"/></div>
</c:forEach>

<form method="POST" action="../exec/newuser">
 <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
 <table border="0" cellspacing="2" cellpadding="2">
  <tr><td>User name: </td><td><input name="login" type="text" size="20" value="<c:out value="${register['login']}"/>"></td></tr>
  <tr><td>Email address: </td><td><input name="login" type="text" size="20" value="<c:out value="${register['email']}"/>"></td></tr>

  <tr><td>Password: </td><td><input name="password" type="password" size="20" value=""></td></tr>
  <tr><td>Password again: </td><td><input name="password2" type="password" size="20" value=""></td></tr>

  <tr><td colspan="2">
   <input value="Cancel" name="cancel" type="submit">
   <input value="Register" name="register" type="submit">
  </td></tr>
 </table>
</form>
