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
      document.getElementById("app.jdbc.user").disabled = checkbox.checked;
      document.getElementById("app.jdbc.password").disabled = checkbox.checked;
  }
  -->
</script>

<table>
  <tr>
    <td><fmt:message key="config.app.jdbc.internal.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.internal"/><br/>
      <input onClick="disableOnCheck(this);" type="checkbox" name="app.jdbc.internal"
        <c:if test="${config.jdbcDriver == 'org.snipsnap.util.MckoiEmbeddedJDBCDriver'}">checked=checked</c:if>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.jdbc.url.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.url"/><br/>
      <input <c:if test="${config.jdbcDriver == 'org.snipsnap.util.MckoiEmbeddedJDBCDriver'}">disabled="disabled"</c:if>
        id="app.jdbc.url" type="text" name="app.jdbc.url" size="40"
        value="<%= ((Configuration)pageContext.findAttribute("config")).getProperties().getProperty("app.jdbc.url") %>">
      <c:if test="${!empty errors['app.jdbc.url']}"><img src="images/attention.jpg"></c:if>
      <div class="hint">(<c:out value="${config.jdbcUrl}"/>)</div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.jdbc.driver.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.driver"/><br/>
      <input <c:if test="${config.jdbcDriver == 'org.snipsnap.util.MckoiEmbeddedJDBCDriver'}">disabled="disabled"</c:if>
        id="app.jdbc.driver" type="text" name="app.jdbc.driver" size="40"
        value="<c:out value='${config.jdbcDriver}'/>">
      <c:if test="${!empty errors['app.jdbc.driver']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.jdbc.user.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.user"/><br/>
      <input <c:if test="${config.jdbcDriver == 'org.snipsnap.util.MckoiEmbeddedJDBCDriver'}">disabled="disabled"</c:if>
        id="app.jdbc.user" type="text" name="app.jdbc.user"
        value="<c:out value='${config.jdbcUser}'/>">
      <c:if test="${!empty errors['app.jdbc.user']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.jdbc.password.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.password"/><br/>
      <input <c:if test="${config.jdbcDriver == 'org.snipsnap.util.MckoiEmbeddedJDBCDriver'}">disabled="disabled"</c:if>
        id="app.jdbc.password" type="password" name="app.jdbc.password">
      <c:if test="${!empty errors['app.jdbc.password']}"><img src="images/attention.jpg"></c:if><br/>
      <div class="hint">
        <c:if test="${not empty config.jdbcPassword}">
          <fmt:message key="config.password.set" />
        </c:if>
      </div>
    </td>
  </tr>
</table>
