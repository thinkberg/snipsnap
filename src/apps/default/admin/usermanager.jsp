<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:import url="/admin/menu.jsp"/>
<div class="admin">

 <table class="wiki-table" width="100%" border="0" cellpadding="3" cellspacing="0">
  <tr>
    <th width="100%">User name</th><th>Last Login</th><th>Email</th><th>Roles</th><th>Status</th><th colspan="2">Action</th>
  </tr>
  <c:forEach items="${usermanager.all}" var="user" varStatus="idx">
    <tr <c:choose>
      <c:when test="${idx.count mod 2 == 0}">class="table-odd"</c:when>
      <c:otherwise>class="table-even"</c:otherwise>
     </c:choose>>
      <td><b><i><a href="<c:url value='/space/${user.login}'/>"><c:out value="${user.login}"/></a></i></b></td>
      <td><c:out value="${user.lastLogin}"/></td>
      <td>
        <c:if test="${user.email != null}">
          <a href="mailto:<c:out value="${user.email}"/>"><c:out value="${user.email}"/></a>
        </c:if>
      </td>
      <td><span class="nobr"><c:out value="${user.roles}"/></span></td>
      <td><span class="nobr"><c:out value="${user.status}"/></span></td>
      <td>
        <form method="post" action="<c:url value='/manager/user'/>">
          <input type="hidden" name="command" value="edit"/>
          <input type="hidden" name="login" value="<c:out value='${user.login}'/>"/>
          <input type="submit" name="ok" value="Edit"/>
        </form>
      </td>
      <td>
        <c:if test="${config.adminLogin != user.login}">
          <form method="post" action="<c:url value='/manager/user'/>">
            <input type="hidden" name="command" value="remove"/>
            <input type="hidden" name="login" value="<c:out value='${user.login}'/>"/>
            <input style="color: red" type="submit" name="ok" value="Remove"/>
          </form>
        </c:if>
      </td>
    </tr>
  </c:forEach>
  <tr>
    <td colspan="7">
      <form method="GET" action="<c:url value='/manager/newuser.jsp'/>">
        <input type="submit" name="ok" value="Add New User"/>
      </form>
    </td>
  </tr>
 </table>
</div>
