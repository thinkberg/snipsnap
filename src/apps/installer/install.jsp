<%--
  ** Initial installation ...
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1>Welcome to your SnipSnap Installation</h1>

<c:forEach items="${errors}" var="error">
  <span class="error"><c:out value="${error.value}"/></span><br/>
</c:forEach>
<br/>

<c:if test="${serverAdmin == null}">
  <b>
    <i>Attention:</i> The user name and password you use here will be used for securing the installer. Next time
    you use it you must authenticate using the information entered here.
  </b>
</c:if>

<br/>
<form method="post" action="../exec/install">
  <table border="0" cellpadding="2" cellspacing="2">
    <tr>
      <td colspan="3" class="table-header">Application Name</td>
    </tr>
    <tr <c:if test="${errors['appname'] != null}">class="error-position"</c:if>>
      <td width="150" valign="top">Name:</td>
      <td valign="top"><input name="appname" type="text" value="<c:out value='${config.name}' default=''/>"></td>
      <td valign="top">Insert the name of your application here.<br/>
      It will appear big on the sites head and is used as Logo if you do not provide an image.</td>
    </tr>
    <tr <c:if test="${errors['tagline'] != null}">class="error-position"</c:if>>
      <td valign="top"><span class="nobr">Website Tagline:</span></td>
      <td valign="top"><input name="tagline" type="text" value=""></td>
      <td valign="top">Give your Website a tagline.<br/>
      The tagline should describe your site in
      a very short sentence, like <i>"Stories about Brian."</i> or
      <i>"Brians Holistic Braindump."</i></td>
    </tr>
    <tr <c:if test="${errors['logoimage'] != null}">class="error-position"</c:if>>
      <td valign="top"><span class="nobr">Logo Image Name:</span></td>
      <td valign="top"><input name="logoimage" type="text" value=""></td>
      <td valign="top"><b>(optional)</b> If you already have a logo for your site put the name here. Leave
      empty if you want to use the Name above as Logo.<br/>
      If it is a PNG image, just use the name without the extension. For <i>"Logo.png"</i> use <i>"Logo"</i>.
      Any other format should include the extension: For <i>"Logo.jpg"</i> use <i>"Logo.jpg"</i>.
      The file itself should be placed in the images directory of your installed application.</td>
    </tr>
    <tr <c:if test="${errors['skin'] != null}">class="error-position"</c:if>>
      <td valign="top"><span class="nobr">Theme:</span></td>
      <td valign="top"><input name="skin" type="radio" value="blue" checked="checked"> Blue Life<br/>
      <img src="<c:url value='/images/blue.png'/>" alt="Blue Life Screenshot"/></td>
      <td valign="top">The theme to use for your installation.</td>
    </tr>
    <tr>
      <td colspan="3" class="table-header">Administrator</td>
    </tr>
    <tr <c:if test="${errors['login'] != null}">class="error-position"</c:if>>
      <td>User Name:</td>
      <td><input name="username" type="text" value="<c:out value='${config.adminLogin}' default=''/>"></td>
    </tr>
    <tr>
      <td>Email Address:</td>
      <td><input name="email" type="text" value="<c:out value='${config.adminEmail}' default=''/>"></td>
    </tr>
    <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
      <td>Password:</td>
      <td><input name="password" type="password" value=""></td>
    </tr>
    <tr <c:if test="${errors['password'] != null}">class="error-position"</c:if>>
      <td><span class="nobr">Password (verification):</span></td>
      <td><input name="password2" type="password" value=""></td>
    </tr>
    <tr>
      <td colspan="3" class="table-header">Application</td>
    </tr>
    <tr <c:if test="${errors['host'] != null}">class="error-position"</c:if>>
      <td valign="top">Virtual Host:</td>
      <td valign="top"><input name="host" type="text" value="<c:out value='${config.host}' default=''/>"></td>
      <td valign="top"><b>(optional)</b><br/>
        The virtual host the server should accept requests for.
        Leave blank if you want to accept requests for all possible
        host names your server has (default).
      </td>
    </tr>
    <tr <c:if test="${errors['port'] != null}">class="error-position"</c:if>>
      <td valign="top">Port Number:</td>
      <td valign="top"><input name="port" type="text" value="<c:out value='${config.port}' default='8668'/>"></td>
      <td valign="top"><b>(optional)</b></td>
    </tr>
    <tr>
      <td valign="top">Path on Server:</td>
      <td valign="top"><input name="context" type="text" value="<c:out value='${config.contextPath}' default=''/>"></td>
      <td valign="top"><b>(optional)</b><br/>
        The relative path on your server where the application resides.
        If you enter <i>/foo</i> then your application listens for requests
        like: <i>http://localhost:8668/foo/space/start</i>. Leave the
        default if this is a standalone server.
      </td>
    </tr>
    <tr>
      <td valign="top">Real URL:</td>
      <td valign="top"><input name="domain" type="text" value="<c:out value='${config.domain}' default=''/>"></td>
      <td valign="top"><b>(optional)</b><br/>
        If SnipSnap is running behind a proxy or dyndns set the real world accessible URL to this instance
        of snipsnap, e.g. 'http://snipsnap.org'
      </td>
    </tr>

    <input name="usemckoi" type="hidden" value="checked">
    <input name="jdbc" type="hidden" value="">
    <input name="driver" type="hidden" value="">

    <%-- DEACTIVATED
    <tr>
      <td valign="top">Use Mckoi Database:</td>
      <td valign="top"><input name="usemckoi" type="checkbox" checked="checked"></td>
      <td valign="top">(default)<br/>
        Use Mckoi Database. <b>It is not supported right now to change that!</b>
      </td>
    </tr>
    <tr>
      <td valign="top">Database URL:</td>
      <td valign="top"><input name="jdbc" type="text" value="<c:out value='${config.JDBCURL}' default=''/>"></td>
      <td valign="top"><b>(optional)</b><br/>
        The JDBC URL to use for this installation. <i>Leave empty if you use the built-in database.</i>
      </td>
    </tr>
    <tr>
      <td valign="top">JDBC Driver Class:</td>
      <td valign="top"><input name="driver" type="text" value="<c:out value='${config.JDBCDriver}' default=''/>"></td>
      <td valign="top"><b>(optional)</b><br/>
        The JDBC Driver to use for this database connection.
      </td>
    </tr>
    --%>
    <tr>
      <td colspan="3">
        <input type="submit" value="Install SnipSnap"/>
      </td>
    </tr>
  </table>
</form>
