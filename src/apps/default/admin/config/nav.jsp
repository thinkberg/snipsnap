<%--
  ** Navigation
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<input type="hidden" name="step" value="<c:out value='${step}'/>"/>
<c:if test="${step != 'application'}">
  <input type="submit" name="previous" value="<fmt:message key="admin.config.nav.previous"/>">
</c:if>
<c:choose>
  <c:when test="${not empty finish}">
    <input type="submit" name="finish" value="<fmt:message key="admin.config.nav.finish"/>">
    <c:if test="${empty advanced}">
      <input type="hidden" name="advanced" value="true">
      <input type="submit" name="next" value="<fmt:message key="admin.config.nav.advanced"/>">
    </c:if>
  </c:when>
  <c:otherwise>
    <input type="submit" name="next" value="<fmt:message key="admin.config.nav.next"/>">
  </c:otherwise>
</c:choose>
