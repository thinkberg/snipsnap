<!--
  ** Menu template
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ page import="com.neotis.snip.SnipSpace,
                 java.util.Iterator,
                 com.neotis.snip.Snip,
                 com.neotis.date.Month,
                 com.neotis.app.Application,
                 com.neotis.user.User"%>
<jsp:useBean id="user" scope="request" class="com.neotis.jsp.UserBean">
  <jsp:setProperty name="user" property="session" value="<%= session %>"/>
</jsp:useBean>

<table class="menu" width="100%" border="0" cellpadding="4" cellspacing="1">
 <tr><td class="menuitem">Start<td></tr>
 <tr><td class="menuitem">Index<td></tr>
 <tr><td class="menuitem">Search<td></tr>
 <tr><td>
  <!-- replace this with a JSTL tag ala <s:checkUser role="anonymous"/> -->
  <% if(user.isAuthenticated()) { %>
    logged in as <a href="/space/<%= user.getLogin() %>"><%= user.getLogin() %></a> | <a href="/exec/authenticate?logoff=true">logoff</a>
    <br>
    <a href="/exec/post">post comment</a>
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
  <!-- replace this with a JSTL tag ala <s:recent/> -->
  <%
   SnipSpace space = SnipSpace.getInstance();
   Iterator iterator = space.getChanged().iterator();
   while (iterator.hasNext()) {
     Snip snip = (Snip)iterator.next(); %>
  <a href="/space/<%= snip.getName() %>"><%= snip.getName() %></a><br/>
  <% } %>
  </p>

 </td></tr>
 <tr><td>
  <p>
  <!-- replace this with a JSTL tag ala <s:calendar/> -->
  <% Month m = new Month(); %>
  <%= m.getView() %>
  </p>

  <p>
  <!-- replace this with a JSTL tag ala <s:blogrolling/> -->
   <% Snip rolling = space.load("snipsnap-blogrolling"); %>
   <%= rolling.toXML() %>
  </p>
 </td></tr>

</table>
