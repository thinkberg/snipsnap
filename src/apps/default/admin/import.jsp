<%--
  ** Import Snips/Users.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:import url="/admin/menu.jsp"/>

<div id="admin">
 <c:forEach items="${errors}" var="error">
  <div class="error"><c:out value="${error.value}"/></div>
 </c:forEach>

 <h2>Import into Database</h2>
 <form id="form" method="POST" action="<c:url value='/exec/admin/import'/>" enctype="multipart/form-data">
  <table class="wiki-table" border="0" cellspacing="0" cellpadding="2">
   <tr>
    <td valign="top">XML File with Users/Snips:</td>
    <td><input type="file" name="input" accept="text/xml"></td>
   </tr>
   <tr>
    <td valign="top">Overwrite existing content?</td>
    <td><input type="checkbox" name="overwrite" checked="checked"></td>
   </tr>
   <tr><td class="form-buttons" colspan="2">
    <input value="Import" name="ok" type="submit" tabindex="0"/>
   <input value="Cancel" name="cancel" type="submit" tabindex="0"/>
   </td></tr>
  </table>
 </form>
</div>