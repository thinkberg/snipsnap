<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">User Management</h1>

<table width="100%" border="0" cellpadding="3" cellspacing="0">
  <c:forEach items="${usermanagers}" var="um">
    <tr bgcolor="#aaaaaa">
      <td colspan="7">
        <c:choose>
          <c:when test="${context == um.key}">
            <b>(-) Application Context: <c:out value="${um.key}"/></b>
          </c:when>
          <c:otherwise>
            <a href="../exec/user?context=<c:out value='${um.key}'/>">(+) Application Context: <c:out value="${um.key}"/></a>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <c:if test="${context == um.key}">
      <tr class="table-header">
        <td bgcolor="#aaaaaa"></td><td width="100%">User name</td><td>Email</td><td>Roles</td><td>Status</td><td colspan="2">Action</td>
      </tr>
      <c:forEach items="${um.value.all}" var="user" varStatus="idx">
        <tr class="table-<c:out value='${idx.count mod 2}'/>">
          <td bgcolor="#aaaaaa"></td>
          <td><c:out value="${user.login}"/></td>
          <td><c:out value="${user.email}"/></td>
          <td>
            <nobr><c:forEach items="${user.roles}" var="role"><c:out value="${role} "/></c:forEach></nobr>
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
              <input type="submit" name="ok" value="Remove">
            </form>
          </td>
        </tr>
      </c:forEach>
    </c:if>
  </c:forEach>
</table>

