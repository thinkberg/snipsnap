<%--
  ** Admin menu.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<div class="snip-title">
  <h1 class="snip-name">Administrative Interface (<c:out value="${config.name}"/>)</h1>
</div>

<div class="admin-menu">
 <div class="menu-items">
  <c:choose>
   <c:when test="${page == 'admin/application.jsp'}"><div class="menu-active">Overview</div></c:when>
   <c:otherwise><div class="menu-in active"><a href="<c:out value='manager/'/>">Overview</a></div></c:otherwise>
  </c:choose>
  <c:choose>
   <c:when test="${page == 'admin/usermanager.jsp' || page == 'admin/user.jsp'}"><div class="menu-active">User Management</div></c:when>
   <c:otherwise><div class="menu-inactive"><a href="<c:out value='manager/usermanager.jsp'/>">User Management</a></div></c:otherwise>
  </c:choose>
  <c:choose>
   <c:when test="${page == 'admin/export.jsp'}"><div class="menu-active">Export</div></c:when>
   <c:otherwise><div class="menu-inactive"><a href="<c:out value='manager/export.jsp'/>">Export</a></div></c:otherwise>
  </c:choose>
  <c:choose>
   <c:when test="${page == 'admin/import.jsp'}"><div class="menu-active">Import</div></c:when>
   <c:otherwise><div class="menu-inactive"><a href="<c:out value='manager/import.jsp'/>">Import</a></div></c:otherwise>
  </c:choose>
 </div>
</div>
