<%--
  ** Server Bootstrap Installer login page
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<form class="form" method="post" action="<c:url value='/'/>">
  <fmt:message key="install.server.password"/> <input type="password" name="password">
</form>
