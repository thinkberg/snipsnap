<!--
  ** Initial installation ...
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h3>Welcome to your SnipSnap Installation</h3>

<c:forEach items="${errors}" var="error">
  <span class="error"><c:out value="${error}"/></span><br>
</c:forEach>

<c:choose>
  <c:when test="${config.configured}">
    Your Application is already installed. To make changes use the menu.
    You can remove the existing application by deleting the <b>app and db</b>
    directories and the <b>conf/local.conf</b> file.
  </c:when>
  <c:otherwise>
    <form method="POST" action="/admin/exec/install">
      <table border="0" cellpadding="2" cellspacing="0">
        <tr>
          <td colspan="2" class="table-header">Administrator</td>
        </tr>
        <tr>
          <td>User Name:</td>
          <td><input name="username" type="text" value="<c:out value='${config.userName}' default=''/>"></td>
        <tr>
        <tr>
          <td>Email Address:</td>
          <td><input name="email" type="text" value="<c:out value='${config.email}' default=''/>"></td>
        <tr>
        <tr>
          <td>Password:</td>
          <td><input name="password" type="password" value=""></td>
        <tr>
        <tr>
          <td><nobr>Password (verification):</nobr></td>
          <td><input name="password2" type="password" value=""></td>
        <tr>
        <tr>
          <td colspan="2" class="table-header">Application</td>
        </tr>
        <tr>
          <td>Host Name:</td>
          <td><input name="host" type="text" value="<c:out value='${config.host}' default=''/>"></td>
        <tr>
        <tr>
          <td>Port Number:</td>
          <td><input name="port" type="text" value="<c:out value='${config.port}' default='80'/>"></td>
        <tr>
        <tr>
          <td>Path on Server:</td>
          <td><input name="context" type="text" value="<c:out value='${config.contextPath}' default='/'/>"></td>
        <tr>
        <tr>
          <td colspan="2">
            <input type="submit" value="Install"/>
          </td>
        </tr>
      </table>
    </form>
  </c:otherwise>
</c:choose>