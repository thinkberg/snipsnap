<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">Installed Applications</h1>

<c:forEach items="${errors}" var="error">
  <div class="error"><c:out value="${error.value}"/></div>
</c:forEach>

<table border="0" cellpadding="3" cellspacing="0">
  <tr class="table-header"><td>Application</td><td>Host</td><td>Path</td><td>Database</td><td>Admin</td></tr>
  <c:forEach items="${serverApplications}" var="config" varStatus="idx" >
    <c:set value="${config.value}" var="cfg"/>
    <tr class="table-<c:out value='${idx.count mod 2}'/>">
      <td>
        <a href="<c:out value='${cfg.url}'/>"><c:out value="${config.key}"/></a>
      </td>
      <td><c:out value="${cfg.host}" default="*" /><c:out value=":${cfg.port}" default=""/></td>
      <td><c:out value="${cfg.contextPath}"/></td>
      <td><c:out value="${cfg.JDBCURL}"/></td>
      <td>
        <a href="<c:out value='${cfg.url}'/>/space/<c:out value='${cfg.adminLogin}'/>"><c:out value="${cfg.adminLogin}"/></a> (<c:out value="${cfg.adminEmail}" default="no email"/>)</td>
    </tr>
  </c:forEach>
  <tr><td colspan="5">
    <form method="post" action="<c:url value='/exec/install.jsp'/>">
      <input type="submit" name="ok" value="Install New Application">
    </form>
  </td></tr>
  <form method="post" action="<c:url value='/exec/shutdown'/>">
    <tr class="table-header"><td colspan="8">Server Shutdown</td></tr>
    <tr><td colspan="8">(<i>The administrator user name and password is required again to shut down!</i>)</td></tr>
    <tr>
      <td colspan="4"><b>Admin/Password:</b>
      <input name="login" type="text" size="20" value="" tabindex="0">
      <input name="password" type="password" size="20" value="" tabindex="0"></td>
      <td colspan="4"><input style="color: red" value="Shutdown Server" name="ok" type="submit" tabindex="0"></td>
    </tr>
  </form>
</table>

