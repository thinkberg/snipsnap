<!--
  ** Admin menu.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:if test="${config.configured && admin != null}">
  <table width="100%" class="menu" border="0" cellpadding="8" cellspacing="0">
    <tr>
      <!-- overview -->
      <c:choose>
        <c:when test="${admin != null && page != '/welcome.jsp'}">
          <td align="center" width="25%" class="menuitem-inactive">
            <a href="../">Overview</a>
          </td>
        </c:when>
        <c:otherwise>
          <td align="center" width="25%" class="menuitem-active">
            Overview
          </td>
        </c:otherwise>
      </c:choose>

      <!-- user management -->
      <c:choose>
        <c:when test="${admin != null && !(page == '/usermanager.jsp' || page == '/user.jsp')}">
          <td align="center" width="25%" class="menuitem-inactive">
            <a href="../exec/user">User Management</a>
          </td>
        </c:when>
        <c:otherwise>
          <td align="center" width="25%" class="menuitem-active">
            User Management
          </td>
        </c:otherwise>
      </c:choose>

      <!-- updating application -->
      <c:choose>
        <c:when test="${admin != null && page != '/update.jsp'}">
          <td align="center" width="25%" class="menuitem-inactive">
            Update
          </td>
        </c:when>
        <c:otherwise>
          <td align="center" width="25%" class="menuitem-active">
            Update
          </td>
        </c:otherwise>
      </c:choose>


      <!-- login/logoff -->
      <c:choose>
        <c:when test="${admin != null && config.configured}">
          <td align="center" width="25%" class="menuitem-inactive">
            <a href="../exec/authenticate?logoff=true">Logoff</a>
          </td>
        </c:when>
        <c:otherwise>
          <td align="center" width="25%" class="menuitem-active">
            Login
          </td>
        </c:otherwise>
      </c:choose>
    </tr>
  </table>
</c:if>


