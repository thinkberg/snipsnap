<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h3>SnipSnap Übersicht</h3>

<table border="0" cellpadding="2" cellspacing="0">
  <tr><td bgcolor="table-header" colspan="3">Konfigurierte Applikationen</td></tr>

  <tr><td>Server</td><td>Context Path</td><td>Hosts</td></tr>
  <c:forEach items="${servers}" var="server">
    <tr>
      <td colspan="3"><c:out value="${server}"/></td>
    </tr>
    <c:forEach items="${server.contexts}" var="context">
      <tr>
        <td></td>
        <td><c:out value="${context.contextPath}"/></td>
        <td>
          <c:forEach items="${context.virtualHosts}" var="host"/>
            <c:out value="${host}" default="any"/>
          </c:forEach>
      </tr>
    </c:forEach>
  </c:forEach>
</table>
  <hr>

  <form method="POST" action="../exec/shutdown">
    <table border="0" cellspacing="2" cellpadding="2">
      <tr><td>Admin name: </td><td><input name="login" type="text" size="20" value="" tabindex="0"></td></tr>
      <tr><td>Password: </td><td><input name="password" type="password" size="20" value="" tabindex="0"></td></tr>
      <tr><td colspan="2" align="right">
        <input value="Shutdown" name="ok" type="submit" tabindex="0">
        <input value="Cancel" name="cancel" type="submit" tabindex="0">
      </td></tr>
   </table>
  </form>

