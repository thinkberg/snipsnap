<%--
  ** Template for adding Labels
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Authenticated" permission="Edit" snip="${snip}">
  <p>Add a label to <b><c:out value="${snip.title}"/></b> (step 2/2)</p>
  <form name="form" method="post" action="exec/storelabel">
    <table border="0" cellpadding="0" cellspacing="2">
      <tr><td><c:out value="${label.inputProxy}" escapeXml="false"/></td></tr>
      <tr><td align="right">
        <input value="Add Label" name="save" type="submit"/>
        <input value="Cancel" name="cancel" type="submit"/>
      </td></tr>
    </table>
    <input name="snipname" type="hidden" value="<c:out value="${snip.name}"/>"/>
    <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
    <input name="labeltype" type="hidden" value="<c:out value="${label.type}"/>"/>
  </form>
</s:check>

<s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <a href="exec/login.jsp">Please login!</a>
</s:check>
