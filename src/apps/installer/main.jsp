<!--
  ** Initial installation ...
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ page pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<% response.setContentType("text/html; charset=UTF-8"); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="install.title"/> :: <fmt:message key="install.step.${step}"/></title>
    <link type="text/css" href="default.css" rel="STYLESHEET"/>
  </head>
  <body>
    <div class="header">
      <c:if test="${not empty authenticated}">
        <div class="info">
          <ul><li><c:out value="${applications}"/> installed</li></ul>
        </div>
      </c:if>
      <img src="images/snipsnap-logo.png"/>
    </div>
    <form class="form" method="post" action="<c:url value='/'/>">
      <table class="configuration">
        <tr>
          <td rowspan="2" class="guide">
            <div class="guide-menu">
              <ul>
                <li class="current-step"><fmt:message key="install.guide"/></li>
              </ul>
            </div>
            <div class="step-info"><fmt:message key="install.guide.${step}"/></div>
          </td>
          <td class="edit">
            <div class="step"><fmt:message key="install.step.${step}"/></div>
            <c:if test="${not empty errors}">
              <div class="errors">
                <fmt:message key="install.errors"/>
                <ul>
                <c:forEach items="${errors}" var="error">
                  <li>
                   <fmt:message key="install.error.${error.value}">
                     <fmt:param>
                       <c:choose>
                         <c:when test="${empty config.properties[error.key]}">
                           <c:out value="${error.key}"/>
                         </c:when>
                         <c:otherwise>
                          <c:out value="${config.properties[error.key]}"/>
                         </c:otherwise>
                       </c:choose>
                     </fmt:param>
                   </fmt:message>
                  </li>
                </c:forEach>
                </ul>
              </div>
            </c:if>
            <c:import url="${step}.jsp"/>
          </td>
        </tr>
        <tr>
          <td class="navigation">
            <c:choose>
              <c:when test="${step == 'login'}">
                <input type="submit" name="login" value="<fmt:message key="install.button.${step}"/>">
              </c:when>
              <c:otherwise>
                <input type="submit" name="install" value="<fmt:message key="install.button.${step}"/>">
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </table>
    </form>
  </body>
</html>
