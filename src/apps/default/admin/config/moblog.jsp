<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Mobile Blogging Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="i18n.setup" scope="page" />

<table>
  <tr>
    <td><fmt:message key="config.app.mail.blog.password.text"/></td>
    <td>
      <fmt:message key="config.app.mail.blog.password"/><br/>
      <input type="text" name="app.mail.blog.password" value="<c:out value='${newconfig.mailBlogPassword}'/>">
      <c:if test="${!empty errors['app.mail.blog.password']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.mail.pop3.host.text"/></td>
    <td>
      <fmt:message key="config.app.mail.pop3.host"/><br/>
      <input type="text" name="app.mail.pop3.host" size="40" value="<c:out value='${newconfig.mailPop3Host}'/>">
      <c:if test="${!empty errors['app.mail.pop3.host']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.mail.pop3.user.text"/></td>
    <td>
      <fmt:message key="config.app.mail.pop3.user"/><br/>
      <input type="text" name="app.mail.pop3.user" size="40" value="<c:out value='${newconfig.mailPop3User}'/>">
      <c:if test="${!empty errors['app.mail.pop3.user']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.mail.pop3.password.text"/></td>
    <td>
      <fmt:message key="config.app.mail.pop3.password"/><br/>
      <input type="password" name="app.mail.pop3.password" value="<c:out value='${newconfig.mailPop3Password}'/>">
      <c:if test="${!empty errors['app.mail.pop3.password']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.mail.pop3.interval.text"/></td>
    <td>
      <fmt:message key="config.app.mail.pop3.interval"/><br/>
      <input type="text" name="app.mail.pop3.interval" size="5" value="<c:out value='${newconfig.mailPop3Interval}'/>">
      <c:if test="${!empty errors['app.mail.pop3.interval']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>

</table>
