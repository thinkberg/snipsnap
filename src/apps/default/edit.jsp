<%--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Authenticated" permission="Edit" snip="${snip}">
  <h1 class="header"><c:out value="${snip_name}"/></h1>
  <c:if test="${not empty preview}">
    <div class="preview">
      <c:out value="${preview}" escapeXml="false"/>
    </div>
  </c:if>
  <form name="f" method="POST" action="../exec/store">
    <table border="0" cellpadding="0" cellspacing="2">
      <tr><td><textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}"/></textarea></td></tr>
      <tr><td align="right">
        <input value="Preview" name="preview" type="submit"/>
        <input value="Save" name="save" type="submit"/>
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
