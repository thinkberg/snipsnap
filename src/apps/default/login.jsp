<%--
  ** Template for a login screen.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name">Login</h1></div>
 <div class="snip-content">
  <s:check roles="Authenticated" invert="true">
   <%-- display error message --%>
   <c:if test="${error != null}">
    <div class="error"><c:out value="${error}"/></div>
    <div><b>Forgot your password? <a href="<c:out value='${app.configuration.path}'/>/exec/forgot.jsp?login=<c:out value='${tmpLogin}'/>">Reset your password!</a></b></div>
    <c:if test="${app.configuration.allowRegister}">
      <div><b>Not registered? <a href="<c:out value='${app.configuration.path}'/>/exec/register.jsp?login=<c:out value='${tmpLogin}'/>">Register now!</a></b></div>
    </c:if>
    <p/>
   </c:if>
   <%-- the login form --%>
   <form class="form" method="post" action="<c:out value='${app.configuration.path}'/>/exec/authenticate">
    <table>
     <tr><td><label for="login">User name:</label></td><td><input id="login" name="login" type="text" size="20" value="<c:out value='${tmpLogin}'/>" tabindex="0"/></td></tr>
     <tr><td><label for="password">Password:</label></td><td><input id="password" name="password" type="password" size="20" value="" tabindex="0"/></td></tr>
     <tr><td class="form-buttons" colspan="2">
      <input value="Login" name="ok" type="submit" tabindex="0"/>
      <input value="Cancel" name="cancel" type="submit" tabindex="0"/>
     </td></tr>
    </table>
    <input name="referer" type="hidden" value="<c:out value='${referer}' default='${header["REFERER"]}'/>"/>
   </form>
  </s:check>

  <s:check roles="Authenticated">
   You are logged in as <a href="<c:out value='${app.configuration.path}'/>/space/<c:out value='${app.user.login}'/>"><c:out value="${app.user.login}"/></a>.
   Want to <a href="<c:out value='${app.configuration.path}'/>/exec/authenticate?logoff=true">Logoff</a>?
  </s:check>
 </div>
</div>
