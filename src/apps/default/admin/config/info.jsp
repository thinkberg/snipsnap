<%--
  ** Guide Menu
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="i18n.setup" scope="page" />

<div class="info">
  <ul>
    <li><c:out value="${newconfig.name}"/></li>
    <li><c:out value="${newconfig.url}"/></li>
    <li><c:out value="${newconfig.country}(${newconfig.language}) ${newconfig.timezone} ${newconfig.encoding}"/></li>
    <li><c:out value="${newconfig.theme}"/>
    <li>
      <c:out value="${newconfig.adminLogin}"/>
      <c:if test="${not empty newconfig.adminEmail}">
        (<c:out value="${newconfig.adminEmail}"/>)
      </c:if>
    </li>
  </ul>
</div>
