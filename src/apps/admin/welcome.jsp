<!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${admin != null}">
    <h1 class="header">Server / Application Overview</h1>

    (<i>Handle with care, don't stop the /admin context or you will have to kill your server to stop!</i>)
    <table border="0" cellpadding="3" cellspacing="0">
      <tr class="table-header">
        <td>Server</td><td>Application</td><td>Hosts</td><td>Directory</td><td>Action</td>
      </tr>
      <c:forEach items="${servers}" var="server">
        <tr bgcolor="#aaaaaa"><td colspan="5"><c:out value="${server}"/></td></tr>
        <c:forEach items="${server.contexts}" var="context">
          <c:if test="${context.resourceBase != null}">
          <tr>
            <td bgcolor="#aaaaaa"></td>
            <td><c:out value="${context.contextPath}"/></td>
            <td>
              <c:forEach items="${context.virtualHosts}" var="host">
                <c:out value="${host}" default="any"/>
              </c:forEach>
            </td>
            <td><c:out value="${context.resourceBase}"/></td>
            <td>
              <form method="POST" action="../exec/application">
                <input type="hidden" name="server" value="<c:out value='${server}'/>">
                <input type="hidden" name="context" value="<c:out value='${context.contextPath}'/>">
                <c:choose>
                  <c:when test="${context.started}">
                    <input type="submit" name="stop" value="Stop">
                  </c:when>
                  <c:otherwise>
                    <input type="submit" name="start" value="Start">
                  </c:otherwise>
                </c:choose>
                <input type="submit" name="remove" value="Remove">
              </form>
            </td>
          </tr>
          </c:if>
        </c:forEach>
      </c:forEach>
      <form method="POST" action="../exec/shutdown">
        <tr class="table-header"><td colspan="5">Server Shutdown</td></tr>
        <tr>
          <td colspan="4"><b>Admin/Password:</b>
          <input name="login" type="text" size="20" value="" tabindex="0">
          <input name="password" type="password" size="20" value="" tabindex="0"></td>
          <td colspan="3" align="right"><input value="Shutdown Server" name="ok" type="submit" tabindex="0"></td>
        </tr>
      </table>
    </form>

    </table>
    <hr>
  </c:when>
  <c:otherwise>
    Please <a href="../exec/login.jsp">Login</a>!
  </c:otherwise>
</c:choose>