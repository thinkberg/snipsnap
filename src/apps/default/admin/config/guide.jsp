<%@ page import="java.util.List"%>

<%--
  ** Guide Menu
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="i18n.setup" scope="page" />

<div class="guide-menu">
  <div class="guide-title">
    <c:choose>
      <c:when test="${step == 'login'}">
        <!-- nothing -->
      </c:when>
      <c:when test="${not empty configuser && configuser.admin}">
        <fmt:message key="config.guide.setup"/>
      </c:when>
      <c:otherwise>
        <fmt:message key="config.guide.title">
          <fmt:param><%= ((List) pageContext.findAttribute("steps")).size() %></fmt:param>
        </fmt:message>
      </c:otherwise>
    </c:choose>
  </div>
  <ul>
    <c:forEach items="${steps}" var="current" varStatus="status" >
    <li <c:if test="${step == current}">class="current-step"</c:if>>
      <c:choose>
        <c:when test="${not(step == current) && not empty configuser && configuser.admin}">
          <a href="configure?select=<c:out value="${current}"/>">
            <fmt:message key="config.step.${current}"/>
          </a>
        </c:when>
        <c:otherwise><fmt:message key="config.step.${current}"/></c:otherwise>
      </c:choose>
    </li>
    </c:forEach>
  </ul>
  <ul>
    <c:if test="${not empty configuser && configuser.admin}">
      <li><a href="../space/start"><fmt:message key="config.step.back" /></a></li>
    </c:if>
  </ul>
</div>