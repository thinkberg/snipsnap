<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Guide Menu
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<% Configuration cfg = (Configuration)pageContext.findAttribute("config"); %>
<table>
  <tr><th colspan="2"><fmt:message key="admin.config.step.permissions"/></th></tr>
  <tr>
    <td><fmt:message key="admin.config.app.perm.notification"/></td>
    <td>
      <input type="checkbox" name="app.perm.notification"
        <% if(cfg.allow(Configuration.APP_PERM_NOTIFICATION)) { %>checked="checked" <% } %>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.perm.weblogsPing"/></td>
    <td>
      <input type="checkbox" name="app.perm.weblogsPing"
        <% if (cfg.allow(Configuration.APP_PERM_WEBLOGSPING)) { %>checked="checked" <% } %>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.perm.externalImages"/></td>
    <td>
      <input type="checkbox" name="app.perm.weblogsPing"
        <%
          if (cfg.allow(Configuration.APP_PERM_WEBLOGSPING)) {
        %>checked="checked" <%
          }
        %>>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.geoCoordinates"/></td>
    <td><input type="text" value="<c:out value='${config.geoCoordinates}'/>"></td>
  </tr>
</table>
