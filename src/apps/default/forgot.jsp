<%--
  ** Forgotten password page template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name">Reset Password</h1></div>
 <%-- display error message --%>
 <c:forEach items="${errors}" var="error">
  <div class="error"><c:out value="${error.value}"/></div>
 </c:forEach>
 <div class="snip-content">
  Enter your user name to retrieve a password reset key by mail.
  <form class="form" method="post" action="../exec/mailkey">
   <table>
    <tr <c:if test="${errors['login'] != null}">class="error-position"</c:if>>
     <td><label for="login">User name:</label></td><td><input id="login" name="login" type="text" size="20" value="<c:out value="${register['login']}"/>"/></td></tr>
    <tr><td class="form-buttons">
     <input value="Mail reset key" name="mail" type="submit"/>
     <input value="Cancel" name="cancel" type="submit"/>
    </td></tr>
   </table>
  </form>
 </div>
</div>