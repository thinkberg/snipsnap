<%--
  ** Template for password changing from a pw key
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name"><fmt:message key="user.password.change.title"/></h1></div>
 <div class="snip-content">
   <%-- display error message --%>
   <c:if test="${error != null}">
    <div class="error"><fmt:message key="${error}"/><p/></div>
   </c:if>
   <p><fmt:message key="user.password.enter"/></p>
   <%-- the login form --%>
   <form class="form" method="post" action="exec/changepass">
    <table>
     <tr><td><label for="password"><fmt:message key="user.password.new"/></label></td><td><input id="password" name="password" type="password" size="20" value="" tabindex="0"/></td></tr>
     <tr><td><label for="password"><fmt:message key="user.password.verify"/></label></td><td><input id="password2" name="password2" type="password" size="20" value="" tabindex="0"/></td></tr>
     <tr><td class="form-buttons" colspan="2">
      <input value="<fmt:message key='user.passwort.change'/>" name="ok" type="submit" tabindex="0"/>
      <input value="<fmt:message key='dialog.cancel'/>" name="cancel" type="submit" tabindex="0"/>
     </td></tr>
    </table>
    <input name="key" type="hidden" value="<c:out value="${param['key']}"/>"/>
   </form>
 </div>
</div>
