<!--
  ** User Information display/edit.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">Edit User (<c:out value="${user.login}"/>)</h1>

<c:forEach items="${errors}" var="error">
  <div class="error"><c:out value="${error.value}"/></div>
</c:forEach>

<form method="POST" action="../user/edit">
 <input type="hidden" name="command" value="update">
 <input type="hidden" name="context" value="<c:out value='${context}'/>">
 <table border="0" cellspacing="2" cellpadding="2">
  <tr><td class="table-header" colspan="2">User Information</td></tr>
  <tr>
    <td>User Name: </td><td><input readonly name="login" type="text" size="20" value="<c:out value='${user.login}'/>" tabindex="1"></td>
  </tr>
  <tr>
    <td>Email: </td><td><input name="email" type="text" size="20" value="<c:out value='${user.email}'/>" tabindex="0"></td>
  </tr>
  <tr class="table-header"><td colspan="2">Change Password</td></tr>
  <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
    <td>New Password: </td><td><input name="password.new" type="password" size="20" value="" tabindex="0"></td></tr>
  <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
    <td>New Password (verification): </td><td><input name="password2.new" type="password" size="20" value="" tabindex="0"></td></tr>
  <tr class="table-header"><td colspan="2">Roles and Status</td></tr>
  <tr>
    <td>Status: </td><td><input name="status" type="text" size="20" value="<c:out value='${user.status}'/>" tabindex="0"></td>
  </tr>
  <tr <c:if test="${errors['roles'] != null}">class="error-position"</c:if>>
    <td valign="top">Roles: </td>
    <!-- TODO: use checkboxes here ... -->
    <td><input type="text" name="roles" value="<c:out value='${user.roles}'/>"></td>
  </tr>
  <tr><td colspan="2">
   <input value="Update User" name="ok" type="submit" tabindex="0">
   <input value="Cancel/Back" name="cancel" type="submit" tabindex="0">
  </td></tr>
 </table>
</form>
