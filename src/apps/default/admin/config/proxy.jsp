<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Proxy Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<script type="text/javascript" language="Javascript">
  <!--
  function disableOnCheck(checkbox) {
      document.getElementById("app.real.host").disabled = checkbox.checked;
      document.getElementById("app.real.port").disabled = checkbox.checked;
  }
  -->
</script>

<table>
  <tr>
    <td><fmt:message key="config.app.real.autodetect.text"/></td>
    <td>
      <fmt:message key="config.app.real.autodetect"/><br/>
      <input onClick="disableOnCheck(this);" type="checkbox" name="app.real.autodetect" <c:if test="${newconfig.realAutodetect == 'true'}">checked=checked</c:if>>
      <div class="hint">(<fmt:message key="config.detected"/>: <c:out value="${newconfig.url}"/>)</div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.real.protocol.text"/></td>
    <td>
      <fmt:message key="config.app.real.protocol"/><br/>
      <select size="1" name="app.real.protocol">
        <option value="http" <c:if test="${newconfig.properties['app.real.protocol'] == 'http'}">selected="selected"</c:if>>http</option>
        <option value="https" <c:if test="${newconfig.properties['app.real.protocol'] == 'https'}">selected="selected"</c:if>>https</option>
      </select>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.real.host.text"/></td>
    <td>
      <fmt:message key="config.app.real.host"/><br/>
      <input <c:if test="${newconfig.realAutodetect == 'true'}">disabled="disabled"</c:if>
        id="app.real.host" type="text" name="app.real.host" size="40"
        value="<c:out value='${newconfig.properties["app.real.host"]}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.real.port.text"/></td>
    <td>
      <fmt:message key="config.app.real.port"/><br/>
      <input <c:if test="${newconfig.realAutodetect == 'true'}">disabled="disabled"</c:if>
        id="app.real.port" type="text" name="app.real.port" size="40"
        value="<c:out value='${newconfig.properties["app.real.port"]}'/>">
      <c:if test="${!empty errors['app.real.port']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.real.path.text"/></td>
    <td>
      <fmt:message key="config.app.real.path"/><br/>
      <input type="text" name="app.real.path" size="40" value="<c:out value='${newconfig.properties["app.real.path"]}'/>">
    </td>
  </tr>
</table>
