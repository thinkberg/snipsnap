 <%--
  ** status bar
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:set var="percentage" value="${param.statusCurrent * 100 / param.statusMax}" scope="request"/>
<fmt:message key="${param.statusMessage}">
  <fmt:param><fmt:formatNumber maxFractionDigits="0" value="${percentage}"/></fmt:param>
</fmt:message><br/>
<table class="statusbar">
  <tr>
    <c:forEach begin="0" end="100" step="10" var="completed" >
      <td <c:if test="${percentage >= completed}">class="completed"</c:if>></td>
    </c:forEach>
  </tr>
</table>