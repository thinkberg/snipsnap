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
  <tr>
    <td><fmt:message key="config.app.start.snip.text"/></td>
    <td>
      <fmt:message key="config.app.start.snip"/><br/>
      <input type="text" name="app.start.snip" value="<c:out value='${newconfig.startSnip}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.perm.createSnip.text"/></td>
    <td>
      <fmt:message key="config.app.perm.createSnip"/><br/>
      <input type="checkbox" value="allow" name="app.perm.createSnip" <c:if test="${newconfig.permCreateSnip == 'allow'}">checked="checked"</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.logger.text"/></td>
    <td>
      <fmt:message key="config.app.logger"/><br/>
      <select name="app.logger">
        <option value="org.radeox.util.logging.NullLogger"
          <c:if test="${newconfig.logger == 'org.radeox.util.logging.NullLogger'}">checked="checked"</c:if>>
          <fmt:message key="config.logger.none"/></option>
        <option value="org.radeox.util.logging.SystemErrLogger"
          <c:if test="${newconfig.logger == 'org.radeox.util.logging.SystemErrLogger'}">checked="checked"</c:if>>
          <fmt:message key="config.logger.system.err"/></option>
        <option value="org.radeox.util.logging.SystemOutLogger"
          <c:if test="${newconfig.logger == 'org.radeox.util.logging.SystemOutLogger'}">checked="checked"</c:if>>
          <fmt:message key="config.logger.system.out"/></option>
        <option value="org.snipsnap.util.log.ApplicationLogger"
          <c:if test="${newconfig.logger == 'org.snipsnap.util.log.ApplicationLogger'}">checked="checked"</c:if>>
          <fmt:message key="config.logger.application"/></option>
      </input>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.cache.text"/></td>
    <td>
      <fmt:message key="config.app.cache"/><br/>
      <select disabled="disabled" name="app.cache">
        <option value="full"
          <c:if test="${newconfig.cache == 'full'}">checked="checked"</c:if>>
          <fmt:message key="config.caching.full"/></option>
        <option value="cache"
          <c:if test="${newconfig.cache == 'cache'}">checked="checked"</c:if>>
          <fmt:message key="config.caching.cache"/></option>
      </input>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.encoding.text"/></td>
    <td>
      <fmt:message key="config.app.encoding"/><br/>
      <select disabled="disabled" name="app.encoding">
        <option value="UTF-8" <c:if test="${newconfig.encoding == 'UTF-8'}">checked="checked"</c:if>>UTF-8</option>
        <option value="UTF-16" <c:if test="${newconfig.encoding == 'UTF-16'}">checked="checked"</c:if>>UTF-16</option>
        <option value="ISO-8859-1" <c:if test="${newconfig.encoding == 'ISO-8859-1'}">checked="checked"</c:if>>ISO 8859-1</option>
        <option value="US-ASCII" <c:if test="${newconfig.encoding == 'US-ASCII'}">checked="checked"</c:if>>US-ASCII</option>
      </input>
    </td>
  </tr>
</table>