<%@ page import="snipsnap.api.config.Configuration"%>
 <%--
  ** Basic Settings of the Application
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="i18n.setup" scope="page" />

<table>
  <tr>
    <td><fmt:message key="config.app.name.text"/></td>
    <td>
      <fmt:message key="config.app.name"/><br/>
      <input type="text" name="app.name" value="<c:out value='${newconfig.name}'/>" size="40">
      <c:if test="${!empty errors['app.name']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.tagline.text"/></td>
    <td>
       <fmt:message key="config.app.tagline"/><br/>
       <input type="text" name="app.tagline" value="<c:out value='${newconfig.tagline}'/>" size="40">
       <c:if test="${!empty errors['app.tagline']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.logo.text"/></td>
    <td>
      <fmt:message key="config.app.logo"/><br/>
      <input type="file" name="file" value="<c:out value='${newconfig.logo}'/>" accept="image/*">
      <c:if test="${!empty errors['app.logo']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <c:if test="${not newconfig.configured}">
    <tr>
      <td><fmt:message key="config.usage.text"/></td>
      <td>
        <input type="radio" name="usage" value="public"
          <c:if test="${usage == 'public'}">checked="checked"</c:if>
        ><fmt:message key="config.usage.public"/><br/>
        <input type="radio" name="usage" value="closed"
          <c:if test="${usage == 'closed'}">checked="checked"</c:if>
        ><fmt:message key="config.usage.closed"/><br/>
        <input type="radio" name="usage" value="intranet"
          <c:if test="${usage == 'intranet'}">checked="checked"</c:if>
        ><fmt:message key="config.usage.intranet"/><br/>
        <input type="radio" name="usage" value="custom"
          <c:if test="${usage == 'custom'}">checked="checked"</c:if>
        ><fmt:message key="config.usage.custom"/>
      </td>
    </tr>
  </c:if>
</table>
