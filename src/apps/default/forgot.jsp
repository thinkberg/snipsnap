<%--
  ** Forgotten password page template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name">Reset Password</h1></div>
 <p/>Enter your user name. You will receive a key by email that resets your password.<p/>
 <%-- display error message --%>
 <c:if test="${error != null}">
  <div class="error"><c:out value="${error}"/><p/></div>
 </c:if>
 <div class="snip-content">
  <form class="form" method="post" action="<c:out value='${app.configuration.path}'/>/exec/mailkey">
   <table>
    <tr <c:if test="${errors['login'] != null}">class="error-position"</c:if>>
     <td><label for="login">User name:</label></td><td><input id="login" name="login" type="text" size="20" value="<c:out value="${param['login']}"/>"/></td></tr>
    <tr><td class="form-buttons" colspan="2">
     <input value="Mail Reset Key" name="mail" type="submit"/>
     <input value="Cancel" name="cancel" type="submit"/>
    </td></tr>
   </table>
  </form>
 </div>
</div>