<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">Server / Application Overview</h1>

<c:forEach items="${errors}" var="error">
  <div class="error"><c:out value="${error.value}"/></div>
</c:forEach>
<table border="0" cellpadding="3" cellspacing="0">
  <c:forEach items="${servers}" var="server">
    <tr bgcolor="#aaaaaa"><td colspan="8"><c:out value="${server}"/></td></tr>
    <tr class="table-header">
      <td bgcolor="#aaaaaa"></td><td>Application</td><td>Hosts</td><td colspan="5">Action</td>
    </tr>
    <c:forEach items="${server.contexts}" var="context" varStatus="idx" >
      <tr class="table-<c:out value='${idx.count mod 2}'/>">
        <td bgcolor="#aaaaaa"></td>
        <td width="100%"><c:out value="${context.contextPath}"/></td>
        <td>
          <c:choose>
            <c:when test="${empty context.virtualHosts}">ANY</c:when>
            <c:otherwise>
              <c:forEach items="${context.virtualHosts}" var="host" varStatus="hostsIdx">
                <c:out value="${host}" default="?"/><c:if test="${!hostsIdx.last}">, </c:if>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:if test="${context.started && context.contextPath != '/admin' && usermanagers[context.contextPath] != null}">
            <form method="post" action="../exec/user">
              <input type="hidden" name="server" value="<c:out value='${server}'/>">
              <input type="hidden" name="context" value="<c:out value='${context.contextPath}'/>">
              <input type="submit" name="user" value="Manage User">
            </form>
          </c:if>
        </td>
          <td>
            <c:choose>
              <c:when test="${!context.started && context.contextPath != '/admin'}">
                <form method="post" action="../exec/app/start">
                  <input type="hidden" name="server" value="<c:out value='${server}'/>">
                  <input type="hidden" name="context" value="<c:out value='${context.contextPath}'/>">
                  <input style="color: green" type="submit" name="command" value="Start">
                </form>
              </c:when>
              <c:when test="${context.started && context.contextPath != '/admin'}">
                <form method="post" action="../exec/app/stop">
                  <input type="hidden" name="server" value="<c:out value='${server}'/>">
                  <input type="hidden" name="context" value="<c:out value='${context.contextPath}'/>">
                  <input style="color: red" type="submit" name="command" value="Stop">
                </form>
              </c:when>
            </c:choose>
          </td>
          <td>
            <c:if test="${context.contextPath != '/admin'}">
              <form method="post" action="../exec/app/remove">
                <input type="hidden" name="server" value="<c:out value='${server}'/>">
                <input type="hidden" name="context" value="<c:out value='${context.contextPath}'/>">
                <input style="color: red" type="submit" name="command" value="Remove">
              </form>
            </c:if>
          </td>
        </form>
        <td>
          <c:if test="${context.contextPath != '/admin'}">
            <form method="post" action="../exec/app/update">
              <input type="hidden" name="server" value="<c:out value='${server}'/>">
              <input type="hidden" name="context" value="<c:out value='${context.contextPath}'/>">
              <input type="submit" name="command" value="Update">
            </form>
          </c:if>
        </td>
      </tr>
    </c:forEach>
  </c:forEach>
  <form method="post" action="../exec/shutdown">
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

