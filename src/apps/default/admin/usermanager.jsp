<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">User Management</h1>

<table width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr class="table-header">
    <td bgcolor="#aaaaaa"></td><td width="100%">User name</td><td>Email</td><td>Roles</td><td>Status</td><td colspan="2">Action</td>
  </tr>
  <c:forEach items="${um.value.all}" var="user" varStatus="idx">
    <tr class="table-<c:out value='${idx.count mod 2}'/>">
      <td bgcolor="#aaaaaa"></td>
      <td><b><i><c:out value="${user.login}"/></i></b></td>
      <td>
        <c:if test="${user.email != null}">
          <a href="mailto:<c:out value="${user.email}"/>"><c:out value="${user.email}"/></a>
        </c:if>
      </td>
      <td>
        <nobr><c:out value="${user.roles}"/></nobr>
      <td><c:out value="${user.status}"/></td>
      <td>
        <form method="POST" action="../exec/user/edit">
          <input type="hidden" name="context" value="<c:out value='${um.key}'/>">
          <input type="hidden" name="login" value="<c:out value='${user.login}'/>">
          <input type="submit" name="edit" value="Edit">
        </form>
      </td>
      <td>
        <form method="POST" action="../exec/user/edit">
          <input type="hidden" name="command" value="remove">
          <input type="hidden" name="context" value="<c:out value='${um.key}'/>">
          <input type="hidden" name="login" value="<c:out value='${user.login}'/>">
          <input style="color: red" type="submit" name="ok" value="Remove">
        </form>
      </td>
    </tr>
  </c:forEach>
</table>

