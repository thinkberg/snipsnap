<%--
  ** Forgotten password page template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name"><fmt:message key="login.forgot.title"/></h1></div>
 <p/><fmt:message key="login.forgot.text"/><p/>
 <%-- display error message --%>
 <c:if test="${error != null}">
  <div class="error"><c:out value="${error}"/><p/></div>
 </c:if>
 <div class="snip-content">
  <form class="form" method="post" action="exec/mailkey">
   <table>
    <tr <c:if test="${errors['login'] != null}">class="error-position"</c:if>>
     <td><label for="login"><fmt:message key="login.user.name"/></label></td><td><input id="login" name="login" type="text" size="20" value="<c:out value="${param['login']}"/>"/></td></tr>
    <tr><td class="form-buttons" colspan="2">
     <input value="<fmt:message key="login.forgot.dialog.mailit"/>" name="mail" type="submit"/>
     <input value="<fmt:message key="dialog.cancel"/>" name="cancel" type="submit"/>
    </td></tr>
   </table>
  </form>
 </div>
</div>