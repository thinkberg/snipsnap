<!--
  ** Initial installation ...
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1>Welcome to your SnipSnap Installation</h1>

<c:forEach items="${errors}" var="error">
  <span class="error"><c:out value="${error.value}"/></span><br>
</c:forEach>

<c:choose>
  <c:when test="${config.configured}">
    Your Application is already installed. To make changes use the menu.
    You can remove the existing application by deleting the <b>app and db</b>
    directories and the <b>conf/local.conf</b> file.
  </c:when>
  <c:otherwise>
    <form method="POST" action="../exec/install">
      <table border="0" cellpadding="2" cellspacing="0">
        <tr>
          <td colspan="2" class="table-header">Administrator</td>
        </tr>
        <tr <c:if test="${errors['login'] != null}">class="error-position"</c:if>>
          <td>User Name:</td>
          <td><input name="username" type="text" value="<c:out value='${config.userName}' default=''/>"></td>
        <tr>
        <tr>
          <td>Email Address:</td>
          <td><input name="email" type="text" value="<c:out value='${config.email}' default=''/>"></td>
        <tr>
        <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
          <td>Password:</td>
          <td><input name="password" type="password" value=""></td>
        <tr>
        <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
          <td><span class="nobr">Password (verification):</span></td>
          <td><input name="password2" type="password" value=""></td>
        <tr>
        <tr>
          <td colspan="2" class="table-header">Application</td>
        </tr>
        <tr <c:if test="${errors['host'] != null}">class="error-position"</c:if>>
          <td valign="top">Virtual Host:</td>
          <td valign="top"><input name="host" type="text" value="<c:out value='${config.host}' default=''/>"></td>
          <td valign="top">(optional)<br>
	          The virtual host the server should accept requests for.
	          Leave blank if you want to accept requests for all possible
	          host names your server has (default).
	        </td>
        <tr>
        <tr <c:if test="${errors['port'] != null}">class="error-position"</c:if>>
          <td valign="top">Port Number:</td>
          <td valign="top"><input name="port" type="text" value="<c:out value='${config.port}' default='80'/>"></td>
          <td valign="top">(optional)</td>
        <tr>
        <tr>
          <td valign="top">Path on Server:</td>
          <td valign="top"><input name="context" type="text" value="<c:out value='${config.contextPath}' default='/'/>"></td>
          <td valign="top">(optional)<br>
	    The relative path on your server where the application resides.
	    If you enter <i>/foo/</i> then your application listens for requests
	    like: <i>http://localhost:8668/foo/space/start</i>. Leave the
	    default if this is a standalone server.
	  </td>
        <tr>
        <tr>
          <td colspan="2">
            <input type="submit" value="Install SnipSnap"/>
          </td>
        </tr>
      </table>
    </form>
  </c:otherwise>
</c:choose>
