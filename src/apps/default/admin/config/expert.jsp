<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration,
                 java.text.SimpleDateFormat"%>
 <%--
  ** Expert Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr><th colspan="2"><fmt:message key="config.step.expert"/></th></tr>
  <tr>
    <td><fmt:message key="config.app.start.snip.text"/></td>
    <td>
      <fmt:message key="config.app.start.snip"/><br/>
      <input type="text" name="app.start.snip" value="<c:out value='${config.startSnip}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.perm.createSnip.text"/></td>
    <td>
      <fmt:message key="config.app.perm.createSnip"/><br/>
      <input type="checkbox" name="app.perm.createSnip" <c:if test="${config.permCreateSnip == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.file.path.text"/></td>
    <td>
      <fmt:message key="config.app.file.path"/><br/>
      <input type="text" name="app.file.path" size="40" value="<c:out value='${config.properties["app.file.path"]}'/>">
      <div class="hint">(<c:out value="${config.filePath}"/>)</div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.index.path.text"/></td>
    <td>
      <fmt:message key="config.app.index.path"/><br/>
      <input type="text" name="app.index.path" size="40" value="<c:out value='${config.properties["app.index.path"]}'/>">
      <div class="hint">(<c:out value="${config.indexPath}"/>)</div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.logger.text"/></td>
    <td>
      <fmt:message key="config.app.logger"/><br/>
      <select name="app.logger">
        <option value="org.radeox.util.logging.NullLogger"><fmt:message key="config.logger.none"/></option>
        <option value="org.radeox.util.logging.SystemErrLogger"><fmt:message key="config.logger.system.err"/></option>
        <option value="org.radeox.util.logging.SystemOutLogger"><fmt:message key="config.logger.system.out"/></option>
        <option value="org.snipsnap.util.log.ApplicationLogger"><fmt:message key="config.logger.application"/></option>
      </input>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.cache.text"/></td>
    <td>
      <fmt:message key="config.app.cache"/><br/>
      <select disabled="disabled" name="app.cache">
        <option value="full"><fmt:message key="config.caching.full"/></option>
        <option value="cache"><fmt:message key="config.caching.cache"/></option>
      </input>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.encoding.text"/></td>
    <td>
      <fmt:message key="config.app.encoding"/><br/>
      <select disabled="disabled" name="app.encoding">
        <option value="UTF-8" <c:if test="${config.encoding == 'UTF-8'}">checked="checked"</c:if>>UTF-8</option>
        <option value="UTF-16" <c:if test="${config.encoding == 'UTF-16'}">checked="checked"</c:if>>UTF-16</option>
        <option value="ISO-8859-1" <c:if test="${config.encoding == 'ISO-8859-1'}">checked="checked"</c:if>>ISO 8859-1</option>
        <option value="US-ASCII" <c:if test="${config.encoding == 'US-ASCII'}">checked="checked"</c:if>>US-ASCII</option>
      </input>
    </td>
  </tr>
</table>
