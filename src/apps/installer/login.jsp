<%@ page import="org.snipsnap.config.Configuration"%>
 <!--
  ** Admin login.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">Server Admin Login</h1>

<c:if test="${error != null}">
  <div class="error"><c:out value="${error}"/></div>
</c:if>

<%-- looks like JSTL tries to be too smart and handles Properties extended objects different --%>
<c:choose>
  <c:when test="${serverConfig.adminLogin == null}">
   <h3>You do not have a server administrator!</h3>
   <span class="error">Protect your server by creating an administrator now!</span>
    <form method="POST" action="../exec/authenticate">
     <table border="0" cellspacing="2" cellpadding="2">
      <tr><td>Admin Login: </td><td><input name="login" type="text" size="20" value="" tabindex="0"></td></tr>
      <tr><td>Password: </td><td><input name="password" type="password" size="20" value="" tabindex="0"></td></tr>
      <tr><td>Password (verification): </td><td><input name="password2" type="password" size="20" value="" tabindex="0"></td></tr>
      <tr><td>Email Address: </td><td><input name="email" type="text" size="20" value="" tabindex="0"></td></tr>
      <tr><td colspan="2">
       <input value="Create Admin" name="create" type="submit" tabindex="0">
      </td></tr>
     </table>
    </form>
  </c:when>
  <c:when test="${empty serverAdmin}">
    <form method="POST" action="../exec/authenticate">
     <table border="0" cellspacing="2" cellpadding="2">
      <tr><td>Admin Login: </td><td><input name="login" type="text" size="20" value="" tabindex="0"></td></tr>
      <tr><td>Password: </td><td><input name="password" type="password" size="20" value="" tabindex="0"></td></tr>
      <tr><td colspan="2">
       <input value="Login" name="ok" type="submit" tabindex="0">
       <input value="Cancel" name="cancel" type="submit" tabindex="0">
      </td></tr>
     </table>
    </form>
  </c:when>
  <c:otherwise>
    You are already logged in! Want to <a href="../exec/authenticate?logoff=true">logout</a>?
  </c:otherwise>
</c:choose>