<!--
  ** weblog post template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:snip load="${param['name']}" id="snip" />

<s:check roles="Authenticated">
 <h1 class="header">Post Comment to <c:out value="${snip.name}"/></h1>
 <form method="POST" action="/exec/storecomment">
  <input name="comment" type="hidden" value="<c:out value="${snip.name}"/>">
  <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
  <br>
  <textarea name="content" type="text" cols="80" rows="20"></textarea><br/>
  <input value="Cancel" name="cancel" type="submit">
  <input value="Comment" name="save" type="submit">
 </form>
</s:check>

<s:check roles="Authenticated" invert="true">
 <a href="/exec/login.jsp">Please login!</a>
</s:check>