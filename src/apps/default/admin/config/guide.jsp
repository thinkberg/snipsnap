<%@ page import="java.util.List"%> <%--
  ** Guide Menu
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="guide-menu">
  <div class="guide-title">
    <fmt:message key="admin.config.guide.title">
      <fmt:param><%= ((List)pageContext.findAttribute("steps")).size() %></fmt:param>
    </fmt:message>
  </div>
  <ul>
    <c:forEach items="${steps}" var="current" varStatus="status" >
    <li <c:if test="${step == current}">class="current-step"</c:if>>
      <fmt:message key="admin.config.step.${current}"/>
    </li>
    </c:forEach>
  </ul>
</div>