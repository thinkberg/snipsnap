<%@ page import="java.util.*,
                 snipsnap.api.config.Configuration"%>
 <%--
  ** Database Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="i18n.setup" scope="page" />

<%
  // put known drivers in session context
  if(null == session.getAttribute("jdbcDriver")) {
    Map knownJdbcDriver = new HashMap();
    knownJdbcDriver.put("jdbc.mckoi", "com.mckoi.JDBCDriver");
    knownJdbcDriver.put("jdbc.mysql", "com.mysql.jdbc.Driver");
    knownJdbcDriver.put("jdbc.postgresql", "org.postgresql.Driver");
    //knownJdbcDriver.put("jdbc.oracle", "com.oracle.jdbc.Driver");
    Iterator driverIt = knownJdbcDriver.keySet().iterator();
    while (driverIt.hasNext()) {
      String driver = (String) driverIt.next();
      try {
        Class.forName((String)knownJdbcDriver.get(driver));
      } catch (Exception e) {
        driverIt.remove();
      } catch(Error err) {
        driverIt.remove();
      }
    }
    session.setAttribute("jdbcDriver", knownJdbcDriver);
  }
%>

<script type="text/javascript" language="Javascript">
  <!--
  function selectOptions(select) {
    var driver = select.options[select.selectedIndex].value;
    if(driver == 'file') {
      showParts(document.getElementsByName("file"));
      hideParts(document.getElementsByName("jdbc"));
      hideParts(document.getElementsByName("mckoi"));
    } else if(driver == 'jdbc.mckoi') {
      showParts(document.getElementsByName("mckoi"));
      hideParts(document.getElementsByName("file"));
      hideParts(document.getElementsByName("jdbc"));
    } else {
      showParts(document.getElementsByName("jdbc"));
      hideParts(document.getElementsByName("file"));
      hideParts(document.getElementsByName("mckoi"));

      var jdbcField = document.getElementById('app.jdbc.driver');
      var jdbcUrl = document.getElementById('app.jdbc.url');
      <c:forEach items="${jdbcDriver}" var="driver">
      if(driver == '<c:out value="${driver.key}"/>') {
        jdbcField.value = '<c:out value="${driver.value}"/>';
      }
      </c:forEach>
      if(driver != 'jdbc') {
        hideParts(document.getElementsByName("jdbcDriver"));
        driver = driver.substring(5, driver.length);
      } else {
        showParts(document.getElementsByName("jdbcDriver"));
        jdbcField.value = 'enter driver class here';
        driver = 'driver';
      }
      jdbcUrl.value = 'jdbc:' + driver + '://server/snipsnapdb'
      if(driver == 'postgresql') {
        jdbcUrl.value = jdbcUrl.value + '?charSet=utf-8';
      }
    }
  }

  function showParts(elements) {
    // iterator over elements and show
    for(var e = 0; e < elements.length; e++) {
      elements[e].style.visibility = "visible";
      elements[e].style.display = "table-row";
    }
  }

  function hideParts(elements) {
    // iterator over elements and
    for(var e = 0; e < elements.length; e++) {
      elements[e].style.visibility = "hidden";
      elements[e].style.display = "none";
    }
  }
  -->
</script>

<table>
  <tr>
    <td><fmt:message key="config.app.database.text"/></td>
    <td>
      <fmt:message key="config.app.database"/><br/>
      <select name="app.database" size="1" onChange="selectOptions(this)">
        <option value="file"
         <c:if test="${newconfig.database == 'file'}">selected="selected"</c:if>><fmt:message key="config.app.database.file"/></option>
        <c:forEach items="${jdbcDriver}" var="driver">
          <option value="<c:out value='${driver.key}'/>"
            <c:if test="${newconfig.database == driver.key}">selected="selected"</c:if>><fmt:message key="config.app.database.${driver.key}"/></option>
        </c:forEach>
        <option value="jdbc" <c:if test="${newconfig.database == 'jdbc'}">selected="selected"</c:if>><fmt:message key="config.app.database.jdbc"/></option>
      </select>
    </td>
  </tr>
  <tr name="jdbc"
    <c:if test="${newconfig.database == 'file' || newconfig.database == 'jdbc.mckoi'}">style="visibility:hidden; display:none"</c:if>>
    <td><fmt:message key="config.app.jdbc.url.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.url"/><br/>
      <input id="app.jdbc.url" type="text" name="app.jdbc.url" size="40"
        value="<%= ((Configuration)pageContext.findAttribute("newconfig")).getGlobals().getProperty(Configuration.APP_JDBC_URL) %>">
      <c:if test="${!empty errors['app.jdbc.url']}"><img src="images/attention.jpg"></c:if>
      <%--<div class="hint">(<c:out value="${newconfig.jdbcUrl}"/>)</div>--%>
    </td>
  </tr>
  <tr name="jdbcDriver"
    <c:if test="${newconfig.database == 'file' || newconfig.database == 'jdbc.postgresql' || newconfig.database == 'jdbc.mckoi'}">style="visibility:hidden; display:none"</c:if>>
    <td><fmt:message key="config.app.jdbc.driver.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.driver"/><br/>
      <input id="app.jdbc.driver" type="text" name="app.jdbc.driver" size="40"
        value="<c:out value='${newconfig.jdbcDriver}'/>">
      <c:if test="${!empty errors['app.jdbc.driver']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr name="jdbc"
    <c:if test="${newconfig.database == 'file' || newconfig.database == 'jdbc.mckoi'}">style="visibility:hidden; display:none"</c:if>>
    <td><fmt:message key="config.app.jdbc.user.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.user"/><br/>
      <input id="app.jdbc.user" type="text" name="app.jdbc.user"
        value="<c:out value='${newconfig.jdbcUser}'/>">
      <c:if test="${!empty errors['app.jdbc.user']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr name="jdbc"
    <c:if test="${newconfig.database == 'file' || newconfig.database == 'jdbc.mckoi'}">style="visibility:hidden; display:none"</c:if>>
    <td><fmt:message key="config.app.jdbc.password.text"/></td>
    <td>
      <fmt:message key="config.app.jdbc.password"/><br/>
      <input id="app.jdbc.password" type="password" name="app.jdbc.password">
      <c:if test="${!empty errors['app.jdbc.password']}"><img src="images/attention.jpg"></c:if><br/>
      <div class="hint">
        <c:if test="${not empty newconfig.jdbcPassword}">
          <fmt:message key="config.password.set" />
        </c:if>
      </div>
    </td>
  </tr>
  <tr name="file"
      <c:if test="${newconfig.database != 'file'}">style="visibility:hidden; display:none"</c:if>>
    <td><fmt:message key="config.app.database.file.text"/></td>
    <td>
      <input type="hidden" name="app.file.store"
        value="<%= ((Configuration) pageContext.findAttribute("newconfig")).getGlobals().getProperty(snipsnap.api.config.Configuration.APP_FILE_STORE) %>">
      <fmt:message key="config.app.database.noconfig"/><br/>
    </td>
  </tr>
  <tr name="mckoi"
      <c:if test="${newconfig.database != 'jdbc.mckoi'}">style="visibility:hidden; display:none"</c:if>>
    <td><fmt:message key="config.app.database.mckoi.text"/></td>
    <td>
      <fmt:message key="config.app.database.noconfig"/><br/>
    </td>
  </tr>
</table>
