 <%--
  ** Mail Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr>
    <td><fmt:message key="config.export.file.text"/></td>
    <td>
      <input type="radio" name="export.file" value="download"
        <c:if test="${empty exportFile || exportFile == 'download'}">checked="checked"</c:if>>
      <fmt:message key="config.export.download"/>
      <c:if test="${!empty errors['export.file']}"><img src="images/attention.jpg"></c:if>
      <input type="radio" name="export.file" value="webinf"
        <c:if test="${exportFile == 'webinf'}">checked="checked"</c:if>>
      <fmt:message key="config.export.webinf"/><br/>
      <div class="hint">
        (<c:out value="${newconfig.webInfDir}"/>)
      </div><br/>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.export.types.text"/></td>
    <td>
      <c:if test="${!empty errors['export.types']}"><img src="images/attention.jpg"><br/></c:if>
      <input type="checkbox" name="export.types" value="snips"
        <c:if test="${empty exportTypes || exportTypeSnips == 'true'}">checked="checked"</c:if>>
      <fmt:message key="config.export.types.snips"/>
      <fmt:message key="config.export.match"/>
      <input type="text" name="export.match" value="<c:out value="${exportMatch}"/>">
      <br/>
      <input type="checkbox" name="export.types" value="users"
        <c:if test="${empty exportTypes || exportTypeUsers == 'true'}">checked="checked"</c:if>>
      <fmt:message key="config.export.types.users"/>
    </td>
  </tr>
  <tr>
    <td></td>
    <td>
     <input type="submit" name="export" value="<fmt:message key="config.export"/>">
    </td>
  </tr>
</table>
