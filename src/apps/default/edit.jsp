<!--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Authenticated">
  <h1 class="header"><c:out value="${snip_name}"/></h1>
  <form method="POST" action="/exec/store">
    <input name="name" type="hidden" value="<c:out value="${snip_name}"/>">
    <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
    <br>
    <textarea name="content" type="text" cols="80" rows="20"><c:out value="${snip.content}"/></textarea><br>
    <input value="Cancel" name="cancel" type="submit">
    <input value="Save" name="save" type="submit">
  </form>
</s:check>

<s:check roles="Authenticated" invert="true">
  <a href="/exec/login">Please login!</a>
</s:check>