<!--
  ** Admin menu.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<span class="snip-name">Administrative Interface (<c:out value="${config.name}"/>)</span>
<p>
<c:url var="base" value="/exec/admin/"/>
<table width="100%" class="snip-table" border="0" cellpadding="5" cellspacing="0">
  <tr>
    <!-- overview -->
    <c:choose>
      <c:when test="${page != '/admin/application.jsp'}">
        <td width="50%" align="middle" class="snip-table-even">
          <b><a href="<c:out value='${base}'/>">Overview</a></b>
        </td>
      </c:when>
      <c:otherwise>
        <td width="50%" align="middle" class="snip-table-odd">
          <b>Overview</b>
        </td>
      </c:otherwise>
    </c:choose>
    <!-- user management -->
    <c:choose>
      <c:when test="${page != '/admin/usermanager.jsp' && page != '/admin/user.jsp'}">
        <td width="50%" align="middle" class="snip-table-even">
          <b><a href="<c:out value='${base}'/>usermanager.jsp">User Management</a></b>
        </td>
      </c:when>
      <c:otherwise>
        <td width="50%" align="middle" class="snip-table-odd">
          <b>User Management</b>
        </td>
      </c:otherwise>
    </c:choose>
  </tr>
</table>
<br>


