<%--
  ** Guide Menu
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="info">
  <ul>
    <li><c:out value="${config.name}"/></li>
    <li><c:out value="${config.url}"/></li>
    <li><c:out value="${config.country}_${config.language} ${config.timezone}"/></li>
    <li><c:out value="${config.adminLogin}"/></li>
  </ul>
</div>
