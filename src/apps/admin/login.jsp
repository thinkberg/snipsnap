<!--
  ** Admin login.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">Admin Login</h1>

<c:if test="${error != null}">
  <div class="error"><c:out value="${error}"/></div>
</c:if>

<form method="POST" action="../exec/authenticate">
 <table border="0" cellspacing="2" cellpadding="2">
  <tr><td>Admin Login: </td><td><input name="login" type="text" size="20" value="" tabindex="0"></td></tr>
  <tr><td>Password: </td><td><input name="password" type="password" size="20" value="" tabindex="0"></td></tr>
  <tr><td colspan="2" align="right">
   <input value="Login" name="ok" type="submit" tabindex="0">
   <input value="Cancel" name="cancel" type="submit" tabindex="0">
  </td></tr>
 </table>
</form>
