<!--
  ** weblog post template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Authenticated">
 <h1 class="header">Post To Weblog</h1>
 <form method="POST" action="/exec/storepost">
  <input name="post" type="hidden" value="weblog">
  <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
  <br>
  <textarea name="content" type="text" cols="80" rows="20"></textarea><br/>
  <input value="Cancel" name="cancel" type="submit">
  <input value="Post" name="save" type="submit">
 </form>
</s:check>

<s:check roles="Authenticaed" invert="true">
 <a href="/exec/login.jsp">Please login!</a>
</s:check>