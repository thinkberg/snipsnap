<%@ page import="org.radeox.util.Encoder"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<table>
 <tr><td><textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}" escapeXml="true"/></textarea></td></tr>
 <tr><td class="form-buttons">
  <input value="<fmt:message key="snip.edit.help"/>" onClick="showHide('help'); return false;" type="submit">
  <input value="<fmt:message key='dialog.preview'/>" name="preview" type="submit"/>
  <input value="<fmt:message key='dialog.save'/>" name="save" type="submit"/>
  <input value="<fmt:message key='dialog.cancel'/>" name="cancel" type="submit"/>
 </td></tr>
</table>

