<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h3>SnipSnap Übersicht</h3>

<table border="0" cellpadding="2" cellspacing="0">
  <tr><td bgcolor="table-header" colspan="2">Konfigurierte Applikationen</td></tr>
  <c:forEach items="${servers}" var="server">
    <tr>
      <td rowspan="<c:out value='${server.contexts}'/>"><c:out value="${server}"/></td>
    </tr>
    <c:forEach items="${server.contexts}" var="context">
      <tr>
        <td><td>
        <td><c:out value="${context.contextPath}"/></td>
        <td>
          <c:forEach items="${context.virtualHosts}" var="host"/>
            <c:out value="${host}" default="ALL"/>,
          </c:forEach>
      </tr>
    </c:forEach>
  </c:forEach>
</table>
