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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <c:if test="${not empty running && not empty running[step]}">
      <meta http-equiv="refresh" content="2; URL=configure?step=<c:out value='${step}'/>"/>
    </c:if>
    <title><fmt:message key="config.title"/> :: <fmt:message key="config.step.${step}"/></title>
    <link type="text/css" href="css/config.css" rel="STYLESHEET"/>
  </head>
  <body>
    <div class="header">
      <c:import url="config/info.jsp"/>
      <img src="images/snipsnap-logo.png"/>
    </div>
    <div class="content">
      <form action="configure" method="POST" enctype="multipart/form-data">
        <table class="configuration">
          <tr>
            <td rowspan="2" class="guide">
              <c:import url="config/guide.jsp"/>
              <div class="step-info"><fmt:message key="config.guide.${step}"/></div>
            </td>
            <td class="edit">
              <div class="step"><fmt:message key="config.step.${step}"/></div>
              <c:if test="${not empty errors}">
                <div class="errors">
                  <c:if test="${empty errors.message}">
                    <fmt:message key="config.errors"/>
                  </c:if>
                  <ul>
                  <c:forEach items="${errors}" var="error">
                    <li>
                     <fmt:message key="config.error.${error.value}">
                       <fmt:param><c:out value="${newconfig.properties[error.key]}"/></fmt:param>
                     </fmt:message>
                    </li>
                  </c:forEach>
                  </ul>
                </div>
              </c:if>
              <c:import url="config/${step}.jsp"/>
            </td>
          </tr>
          <tr><td class="navigation"><c:import url="config/nav.jsp"/></td></tr>
        </table>
      </form>
    </div>
  </body>
</html>
