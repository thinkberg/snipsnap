<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">User Management (<c:out value="${contextPath}"/>)</h1>

<table border="0" cellpadding="3" cellspacing="0">
  <tr class="table-header">
    <td>User name</td><td>Email</td><td>Roles</td><td>Status</td><td>Action</td>
  </tr>
  <c:forEach items="${usermanager.all}" var="user">
    <tr>
      <td><c:out value="${user.login}"/></td>
      <td><c:out value="${user.email}"/></td>
      <td>
        <nobr><c:forEach items="${user.roles}" var="role"><c:out value="${role} "/></c:forEach></nobr>
      <td><c:out value="${user.status}"/></td>
      <td>
        <form method="GET" action="../exec/user">
          <input type="hidden" name="user" value="<c:out value='${user.login}'/>">
          <input type="submit" name="edit" value="Edit">
        </form>
      </td>
    </tr>
  </c:forEach>
</table>



