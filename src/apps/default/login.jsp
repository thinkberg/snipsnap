<!--
  ** Template for a login screen.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<h1 class="header">Login</h1>

<% String login = request.getParameter("login"); %>

<% if(request.getParameter("message") != null) { %>
 <!-- display error message -->

 <div class="error"><%= request.getParameter("message") %></div>
 <table border="0" width="100%" cellpadding="0" cellspacing="2">
 <tr><td>
  <b>Forgot your password? <a href="../exec/passreminder?login=<%= login != null ? login : "" %>">Mail it back!<a/></b>
 </td></tr>
 <tr><td>
  <b>Not registered? <a href="../exec/register.jsp?login=<%= login != null ? login : "" %>">Register!<a/></b>
 </td></tr>
 </table>
 <br>
<% } %>

<form name="f" method="POST" action="../exec/authenticate">
 <table border="0" cellspacing="2" cellpadding="2">
  <tr><td>User name: </td><td><input name="login" type="text" size="20" value="<%= login != null ? login : "" %>" tabindex="0"></td></tr>
  <tr><td>Password: </td><td><input name="password" type="password" size="20" value="" tabindex="0"></td></tr>
  <tr><td colspan="2" align="right">
   <input value="Login" name="ok" type="submit" tabindex="0">
   <input value="Cancel" name="cancel" type="submit" tabindex="0">
  </td></tr>
 </table>
 <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
</form>
