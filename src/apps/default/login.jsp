<%--
  ** Template for a login screen.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div id="snip-header"><h1 class="snip-name">Login</h1></div>

<s:check roles="Authenticated" invert="true">
 <%-- display error message --%>
 <c:if test="${error != null}">
  <div class="error"><c:out value="${error}"/></div>

  <div><b>Forgot your password? <a href="../exec/passreminder?login=<c:out value='${login.login}'/>">Mail it back!<a/></b></div>
  <div><b>Not registered? <a href="../exec/register.jsp?login=<c:out value='${login.login}'/>">Register!<a/></b></div>
 </c:if>

 <div id="login">
  <form name="f" method="POST" action="../exec/authenticate">
   <table border="0" cellspacing="2" cellpadding="2">
    <tr><td><label for="login">User name:</label> </td><td><input name="login" type="text" size="20" value="<c:out value='${login.login}'/>" tabindex="0"/></td></tr>
    <tr><td><label for="password">Password:</label> </td><td><input name="password" type="password" size="20" value="" tabindex="0"/></td></tr>
    <tr><td colspan="2" align="right">
     <input value="Login" name="ok" type="submit" tabindex="0"/>
     <input value="Cancel" name="cancel" type="submit" tabindex="0"/>
    </td></tr>
   </table>
   <input name="referer" type="hidden" value="<c:out value='${referer}' default='${header["REFERER"]}'/>"/>
  </form>
 </div>
</s:check>

<s:check roles="Authenticated">
  You are logged in as <a href="../space/<c:out value='${app.user.login}'/>"><c:out value="${app.user.login}"/></a>.
  Want to <a href="../exec/authenticate?logoff=true">Logoff</a>?
</s:check>
