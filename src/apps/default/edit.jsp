<!--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<jsp:useBean id="snip" scope="request" class="com.neotis.jsp.SnipBean">
  <jsp:setProperty name="snip" property="*"/>
</jsp:useBean>
<jsp:useBean id="user" scope="request" class="com.neotis.jsp.UserBean">
  <jsp:setProperty name="user" property="session" value="<%= session %>"/>
</jsp:useBean>

<% if(user.isAuthenticated()) { %>
 <h1 class="header"><jsp:getProperty name="snip" property="name" /></h1>
 <form method="POST" action="/exec/store">
  <input name="name" type="hidden" value="<%= snip.getName() %>">
  <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
  <br>
  <textarea name="content" type="text" cols="80" rows="20"><jsp:getProperty name="snip" property="content" /></textarea><br/>
  <input value="Cancel" name="cancel" type="submit">
  <input value="Save" name="save" type="submit">
 </form>
<% } else { %>
 <a href="/exec/login">Please login!</a>
<% } %>
