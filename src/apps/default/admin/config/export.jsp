 <%--
  ** Mail Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <c:choose>
    <c:when test="${not empty running && not empty running.export}">
      <tr>
        <td>
          <fmt:message key="config.export.running"/>
          <fmt:message key="config.refresh.text"/>
        </td>
        <td>
          <c:import url="config/statusbar.jsp">
            <c:param name="statusMessage" value="config.status"/>
            <c:param name="statusMax" value="${running.max}"/>
            <c:param name="statusCurrent" value="${running.current}"/>
          </c:import>
          <br/>
          <a href="configure?step=export"><fmt:message key="config.refresh"/></a>
        </td>
      </tr>
    </c:when>
    <c:otherwise>
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
          <table class="export-options">
            <tr><td>
              <c:if test="${!empty errors['export.types']}"><img src="images/attention.jpg"><br/></c:if>
              <input type="checkbox" name="export.types" value="snips"
                <c:if test="${empty exportTypes || exportTypeSnips == 'true'}">checked="checked"</c:if>>
              <fmt:message key="config.export.types.snips"/>
            <td><td>
              <fmt:message key="config.export.match"/><br/>
              <input type="text" name="export.match" value="<c:out value="${exportMatch}"/>"><br/>
              <fmt:message key="config.export.ignore"/><br/>
              <input type="text" name="export.ignore" value="<c:out value="${exportIgnore}"/>">
            </td></tr>
            <tr><td colspan="2">
              <input type="checkbox" name="export.types" value="users"
                <c:if test="${empty exportTypes || exportTypeUsers == 'true'}">checked="checked"</c:if>>
              <fmt:message key="config.export.types.users"/>
            </td></tr>
          </table>
        </td>
      </tr>
      <tr>
        <td></td>
        <td>
         <input type="submit" name="export" value="<fmt:message key="config.export"/>">
        </td>
      </tr>
    </c:otherwise>
  </c:choose>
</table>
