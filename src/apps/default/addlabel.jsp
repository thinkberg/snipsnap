<%--
  ** Template for adding Labels
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Authenticated" permission="Edit" snip="${snip}">
  Add label to <b><c:out value="${snip_name}"/></b>
  <form name="f" method="POST" action="../exec/addlabel">
    <table border="0" cellpadding="0" cellspacing="2">
      <tr><td><input type="text" name="type"/></td><td><input type="text" name="value"/></td></tr>
      <tr><td align="right" colspan="2">
        <input value="Add Label" name="save" type="submit"/>
        <input value="Cancel" name="cancel" type="submit"/>
      </td></tr>
    </table>
    <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
    <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
  </form>
</s:check>

<s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <a href="../exec/login.jsp">Please login!</a>
</s:check>
