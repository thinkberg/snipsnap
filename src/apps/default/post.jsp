<!--
  ** Snip display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<jsp:useBean id="user" scope="request" class="com.neotis.jsp.UserBean" >
  <jsp:setProperty name="user" property="session" value="<%= session %>"/>
</jsp:useBean>


<% if(user.isAuthenticated()) { %>
 <h1 class="header">Post To Weblog</h1>
 <form method="POST" action="/exec/storepost">
  <input name="post" type="hidden" value="weblog">
  <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
  <br>
  <textarea name="content" type="text" cols="80" rows="20"></textarea><br/>
  <input value="Cancel" name="cancel" type="submit">
  <input value="Post" name="save" type="submit">
 </form>
<% } else { %>
 <a href="/exec/login">Please login!</a>
<% } %>
