<%@ page import="org.snipsnap.config.Configuration"%>
 <%--
  ** Basic Settings of the Application
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr><th colspan="2"><fmt:message key="admin.config.step.application"/></th></tr>
  <tr>
    <td><fmt:message key="admin.config.app.name.text"/></td>
    <td>
      <fmt:message key="admin.config.app.name"/><br/>
      <input type="text" name="app.name" value="<c:out value='${config.name}'/>" size="40">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.tagline.text"/></td>
    <td>
       <fmt:message key="admin.config.app.tagline"/><br/>
      <input type="text" name="app.tagline" value="<c:out value='${config.tagline}'/>" size="40">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.logo.text"/></td>
    <td>
      <fmt:message key="admin.config.app.logo"/><br/>
      <input type="file" name="app.logo" value="<c:out value='${config.logo}'/>" accept="image/*">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.usage.text"/></td>
    <td>
      <input type="radio" name="usage" value="public"
        <c:if test="${usage == 'public'}">checked="checked"</c:if>
      ><fmt:message key="admin.config.usage.public"/><br/>
      <input type="radio" name="usage" value="closed"
        <c:if test="${usage == 'closed'}">checked="checked"</c:if>
      ><fmt:message key="admin.config.usage.closed"/><br/>
      <input type="radio" name="usage" value="intranet"
        <c:if test="${usage == 'intranet'}">checked="checked"</c:if>
      ><fmt:message key="admin.config.usage.intranet"/><br/>
      <input type="radio" name="usage" value="custom"
        <c:if test="${usage == 'custom'}">checked="checked"</c:if>
      ><fmt:message key="admin.config.usage.custom"/>
    </td>
  </tr>
</table>
