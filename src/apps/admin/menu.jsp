<!--
  ** Admin menu.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:if test="${config.configured}">
  <table class="menu" border="0" cellpadding="4" cellspacing="1">
    <tr><td class="menuitem"><a href="server">Server</a><td></tr>
    <tr><td class="menuitem"><a href="apps">Application</a><td></tr>
    <tr><td class="menuitem"><a href="user">User</a><td></tr>
  </table>
</c:if>
