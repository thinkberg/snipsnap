<%--
  ** Export Users/Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:import url="/admin/menu.jsp"/>

<c:forEach items="${errors}" var="error">
  <div class="error"><c:out value="${error.value}"/></div>
</c:forEach>

<h2>Export Database</h2>
<form id="form" method="POST" action="<c:url value='/exec/admin/export'/>">
 <table class="wiki-table" border="0" cellspacing="0" cellpadding="2">
  <tr>
   <td valign="top">Select Output:</td>
   <td>
    <input type="radio" name="output" value="application" checked="checked">
    Application Home Directory (<c:out value="${config.name}.snip"/>)<br/>
    <input type="radio" name="output" value="web"> Web Download<br/>
   </td>
  </tr>
  <tr>
   <td valign="top">Select Data</td>
   <td>
    <input type="checkbox" name="data" value="users" checked="checked"> Users
    <input type="checkbox" name="data" value="snips" checked="checked"> Snips
  </tr>
  <tr><td class="form-buttons" colspan="2">
   <input value="Export Database" name="ok" type="submit" tabindex="0"/>
   <input value="Cancel" name="cancel" type="submit" tabindex="0"/>
  </td></tr>
 </table>
</form>
