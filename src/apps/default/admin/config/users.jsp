<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration,
                 org.snipsnap.app.Application,
                 org.snipsnap.container.Components,
                 org.snipsnap.user.UserManager"%>
 <%--
  ** User management
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="users">
  <c:choose>
    <c:when test="${not empty param.edit_user}">
      <c:out value="not implemented yet"/>
    </c:when>
    <c:otherwise>
      <c:import url="config/users.list.jsp"/>
    </c:otherwise>
  </c:choose>
</div>