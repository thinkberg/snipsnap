 <!--
  ** Admin login.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<h1 class="header"><fmt:...> </h1>

<form method="post" action="../exec/authenticate">
 <table border="0" cellspacing="2" cellpadding="2">
  <tr><td>Server Password: </td><td><input name="password" type="password" size="20" value="" tabindex="0"></td></tr>
  <tr><td colspan="2">
   <input value="Login" name="ok" type="submit" tabindex="0">
   <input value="Cancel" name="cancel" type="submit" tabindex="0">
  </td></tr>
 </table>
</form>
