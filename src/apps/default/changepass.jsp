<%--
  ** Template for password changing from a pw key
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name">Change password</h1></div>
 <div class="snip-content">
  <s:check roles="Authenticated" invert="true">
   <%-- display error message --%>
   <c:if test="${error != null}">
    <div class="error"><c:out value="${error}"/><p/></div>
   </c:if>
   <p/>Enter your new password twice:<p/>
   <%-- the login form --%>
   <form class="form" method="post" action="<c:out value='${app.configuration.path}'/>/exec/changepass">
    <table>
     <tr><td><label for="password">Password:</label></td><td><input id="password" name="password" type="password" size="20" value="" tabindex="0"/></td></tr>
     <tr><td><label for="password">Password again:</label></td><td><input id="password2" name="password2" type="password" size="20" value="" tabindex="0"/></td></tr>
     <tr><td class="form-buttons" colspan="2">
      <input value="Change Password" name="ok" type="submit" tabindex="0"/>
      <input value="Cancel" name="cancel" type="submit" tabindex="0"/>
     </td></tr>
    </table>
    <input name="key" type="hidden" value="<c:out value="${param['key']}"/>"/>
   </form>
  </s:check>

  <s:check roles="Authenticated">
   You are logged in as <a href="<c:out value='${app.configuration.path}'/>/space/<c:out value='${app.user.login}'/>"><c:out value="${app.user.login}"/></a>.
   Want to <a href="<c:out value='${app.configuration.path}'/>/exec/authenticate?logoff=true">Logoff</a>?
  </s:check>
 </div>
</div>
