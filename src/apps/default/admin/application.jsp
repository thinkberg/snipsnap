<%@ page import="org.snipsnap.snip.SnipSpace,
                 org.snipsnap.user.UserManager,
                 org.snipsnap.snip.SnipSpaceFactory"%>
 <!--
  ** Welcome screen
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<c:import url="/admin/menu.jsp"/>
<div class="admin">
 <h2>Application Info</h2>
 <table>
  <tr><td><b>Administrator:</b></td><td>
   <a href="<c:url value='/space/${config.adminLogin}'/>"><c:out value="${config.adminLogin}"/></a>
   (<a href="<c:out value='mailto:${config.adminEmail}'/>"><c:out value="${config.adminEmail}"/></a>)
  </td></tr>
  <tr><td><b>Address:</b></td><td>
   <c:out value="${config.url}"/> (<c:out value="${config.host}" default="*"/><c:out value=":${config.port}${config.path}"/>)
  </td></tr>
  <tr><td><b>Registered Users:</b></td><td><%= UserManager.getInstance().getAll().size() %></td></tr>
  <tr><td><b>Stored Snips:</b></td><td><%= SnipSpaceFactory.getInstance().getAll().size() %></td></tr>
 </table>
 <div>
  <h3>Select one of the following actions:</h3>
  <ul class="square">
   <li><a href="<c:url value='/manager/usermanager.jsp'/>">Manage Registered Users</a></li>
   <li><a href="<c:url value='/manager/export.jsp'/>">Export Database to XML</a></li>
   <li><a href="<c:url value='/manager/import.jsp'/>">Import into Database from XML</a></li>
  </ul>
 </div>
</div>

