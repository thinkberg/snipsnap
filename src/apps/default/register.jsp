<!--
  ** Registration page template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<h1 class="header">Register User</h1>

<% String login = request.getParameter("login"); %>
<% String email = request.getParameter("email"); %>


<% if(request.getParameter("message") != null) { %>
 <!-- display error message -->
 <div class="error"><%= request.getParameter("message") %></div>
<% } %>

<form method="POST" action="/exec/newuser">
 <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
 <table border="0" cellspacing="2" cellpadding="2">
  <tr><td>User name: </td><td><input name="login" type="text" size="20" value="<%= login != null ? login : "" %>"></td></tr>
  <tr><td>Email address: </td><td><input name="login" type="text" size="20" value="<%= email != null ? email : "" %>"></td></tr>

  <tr><td>Password: </td><td><input name="password" type="password" size="20" value=""></td></tr>
  <tr><td>Password again: </td><td><input name="password2" type="password" size="20" value=""></td></tr>

  <tr><td colspan="2">
   <input value="Cancel" name="cancel" type="submit">
   <input value="Register" name="register" type="submit">
  </td></tr>
 </table>
</form>
