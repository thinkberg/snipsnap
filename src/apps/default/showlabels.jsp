<%--
  ** Template for managing Labels
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Authenticated" permission="Edit" snip="${snip}">
  <p>Currently associated labels for Snip <b><c:out value="${snip.name}"/></b>:</p>
  <c:out value="${labelsProxy}" escapeXml="false"/>
  <p>Add a label to <b><c:out value="${snip.name}"/></b> (step 1/2):</p>
  <form name="form" method="post" action="<c:out value='${app.configuration.path}'/>/exec/addlabel">
    <table border="0" cellpadding="0" cellspacing="2">
      <tr><td><c:out value="${typesProxy}" escapeXml="false"/></td></tr>
      <tr><td align="right">
        <input value="Next" name="save" type="submit"/>
        <input value="Cancel" name="cancel" type="submit"/>
      </td></tr>
    </table>
    <input name="snipname" type="hidden" value="<c:out value="${snip.name}"/>"/>
    <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
  </form>
</s:check>

<s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <a href="<c:out value='${app.configuration.path}'/>/exec/login.jsp">Please login!</a>
</s:check>
