<%@ page import="com.neotis.snip.SnipSpace,
                 java.util.Iterator,
                 com.neotis.snip.Snip,
                 com.neotis.date.Month,
                 com.neotis.app.Application"%>
<% Application app = (Application)session.getAttribute("app"); %>

<table class="menu" width="100%" border="0" cellpadding="4" cellspacing="1">
 <tr><td class="menuitem">Start<td></tr>
 <tr><td class="menuitem">Index<td></tr>
 <tr><td class="menuitem">Search<td></tr>
 <tr><td>
  <% if(app.getUser() != null && !app.getUser().getLogin().equals("Guest")) { %>
    logged in as <%= app.getUser().getLogin() %> | <a href="/exec/authenticate?logoff=true">logoff</a>
  <% } else { %>

   <form method="POST" action="/exec/authenticate">
    <table border="0" cellspacing="0" cellpadding="0">
     <tr>
      <td>Login: </td>
      <td>Password: </td>
     <tr>
      <td><input name="login" type="text" size="10" value=""></td>
      <td><input name="password" type="password" size="10" value=""></td>
     <tr><td colspan="2">
       <input value="Login" name="ok" type="submit"> <a href="/exec/register">Register!</a>
     </td></tr>
    </table>
   </form><br>

  <% } %>
 <tr><td>
  <b>Recent Changes:</b><br>
  <%
   SnipSpace space = SnipSpace.getInstance();
   Iterator iterator = space.getChanged().iterator();
   while (iterator.hasNext()) {
     Snip snip = (Snip)iterator.next(); %>
  <tr><td><a href="/space/<%= snip.getName() %>"><%= snip.getName() %></a></td></tr>
  <% } %>
  </p>
 </td></tr>
 <tr><td>
  <p>
  <% Month m = new Month(); %>
  <%= m.getView(05,2002) %>
  </p>

  <p>
   <% Snip rolling = space.load("weblog::blogrolling"); %>
   <%= rolling.toXML() %>
  </p>
 </td></tr>

</table>
