<!--
  ** Admin menu.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:url var="base" value="/exec/admin/"/>
<table class="menu" border="0" cellpadding="5" cellspacing="0">
  <tr>
    <!-- overview -->
    <c:choose>
      <c:when test="${page != '/application.jsp'}">
        <td align="right" class="menuitem-inactive">
          <a href="<c:out value='${base}'/>">Overview</a>
        </td>
      </c:when>
      <c:otherwise>
        <td align="right" class="menuitem-active">
          Overview
        </td>
      </c:otherwise>
    </c:choose>
  </tr>
  <tr>
    <!-- user management -->
    <c:choose>
      <c:when test="${page != '/usermanager.jsp' && page != '/user.jsp'}">
        <td align="right" width="25%" class="menuitem-inactive">
          <a href="<c:out value='${base}'/>user">User Management</a>
        </td>
      </c:when>
      <c:otherwise>
        <td align="right" width="25%" class="menuitem-active">
          User Management
        </td>
      </c:otherwise>
    </c:choose>
  </tr>
</table>


