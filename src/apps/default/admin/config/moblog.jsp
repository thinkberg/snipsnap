<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Mobile Blogging Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
  <tr><th colspan="2"><fmt:message key="config.step.moblog"/></th></tr>
  <tr>
    <td><fmt:message key="config.app.mail.blog.password.text"/></td>
    <td>
      <fmt:message key="config.app.mail.blog.password"/><br/>
      <input type="text" name="app.mail.blog.password" value="<c:out value='${config.mailBlogPassword}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.mail.pop3.host.text"/></td>
    <td>
      <fmt:message key="config.app.mail.pop3.host"/><br/>
      <input type="text" name="app.mail.pop3.host" size="40" value="<c:out value='${config.mailPop3Host}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.mail.pop3.user.text"/></td>
    <td>
      <fmt:message key="config.app.mail.pop3.user"/><br/>
      <input type="text" name="app.mail.pop3.user" size="40" value="<c:out value='${config.mailPop3User}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.mail.pop3.password.text"/></td>
    <td>
      <fmt:message key="config.app.mail.pop3.password"/><br/>
      <input type="password" name="app.mail.pop3.password" value="<c:out value='${config.mailPop3Password}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.mail.pop3.interval.text"/></td>
    <td>
      <fmt:message key="config.app.mail.pop3.interval"/><br/>
      <input type="text" name="app.mail.pop3.interval" size="5" value="<c:out value='${config.mailPop3Interval}'/>">
    </td>
  </tr>

</table>
