<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Database Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<script type="text/javascript" language="Javascript">
  <!--
  function disableOnCheck(checkbox) {
      document.getElementById("app.jdbc.url").disabled = checkbox.checked;
      document.getElementById("app.jdbc.driver").disabled = checkbox.checked;
  }
  -->
</script>

<table>
  <tr><th colspan="2"><fmt:message key="config.step.database"/></th></tr>
  <tr>
    <td><fmt:message key="config.app.jdbc.internal.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.internal"/><br/>
      <input onClick="disableOnCheck(this);" type="checkbox" name="app.jdbc.internal" <c:if test="${config.jdbcDriver == 'org.snipsnap.util.MckoiEmbeddedJDBCDriver'}">checked=checked</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.jdbc.url.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.url"/><br/>
      <input <c:if test="${config.jdbcDriver == 'org.snipsnap.util.MckoiEmbeddedJDBCDriver'}">disabled="disabled"</c:if>
        id="app.jdbc.url" type="text" name="app.real.host" size="40"
        value="<c:out value='${config.jdbcUrl}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.jdbc.driver.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.driver"/><br/>
      <input <c:if test="${config.jdbcDriver == 'org.snipsnap.util.MckoiEmbeddedJDBCDriver'}">disabled="disabled"</c:if>
        id="app.jdbc.driver" type="text" name="app.jdbc.driver" size="40"
        value="<c:out value='${config.jdbcDriver}'/>">
    </td>
  </tr>
</table>
