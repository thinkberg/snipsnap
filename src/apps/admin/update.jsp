<%@ page import="java.util.Set"%>
 <%--
  ** Update web application.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<h1 class="header">Update (<c:out value="${context}"/>)</h1>

<c:forEach items="${errors}" var="error">
  <div class="error"><c:out value="${error.value}"/></div>
</c:forEach>

<% Set available = (Set)pageContext.findAttribute("available"); %>
<% if(available != null && !available.isEmpty()) { %>
  <div style="border: 1px solid red; margin: 2px 2px 2px 2px;">
    <form method="POST" action="../app/update">
      <input type="hidden" name="server" value="<c:out value='${server}'/>">
      <input type="hidden" name="context" value="<c:out value='${context}'/>">
      There is an update of the web application available on <a href="http://snipsnap.org">SnipSnap</a>.
      <input type="submit" name="download" value="Click to Download Update">
      <br><c:out value="${available}"/>
    </form>
  </div>
<% } %>

<form method="POST" action="../app/update">
  <input type="hidden" name="server" value="<c:out value='${server}'/>">
  <input type="hidden" name="context" value="<c:out value='${context}'/>">
  <span style="color: green">green</span> files are either unchanged or may be updated from the distribution.<br>
  <span style="color: red">red</span> files have been changed locally and will be
  unpacked as <i>filename</i>.new when selected for update.<br>
  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <tr class="table-header">
      <td>No Update Needed</td><td>Locally Changed</td><td>Updated</td><td>Updated and Locally Changed</td>
    </tr>
    <tr>
      <td width="25%" class="table-0" valign="top">
        <c:forEach items="${unchanged}" var="file">
          <span style="color: green"><c:out value="${file}"/></span><br/>
        </c:forEach>
      </td>
      <td width="25%" class="table-1" valign="top">
        <c:forEach items="${changed}" var="file">
          <span style="color: red"><c:out value="${file}"/></span><br/>
        </c:forEach>
      </td>
      <td width="25%" class="table-0" valign="top">
        <c:forEach items="${installable}" var="file">
          <input type="checkbox" name="install" checked="checked" value="<c:out value='${file}'/>"><span style="color: green"><c:out value="${file}"/></span><br/>
        </c:forEach>
      </td>
      <td width="25%" class="table-1" valign="top">
        <c:forEach items="${updated}" var="file">
          <input type="checkbox" name="extract" value="<c:out value='${file}'/>"><span style="color: red"><c:out value="${file}"/></span><br/>
        </c:forEach>
      </td>
    </tr>
    <tr>
      <td colspan="4">
        <input type="submit" name="check" value="Check Again">
        <% Set installable = (Set)pageContext.findAttribute("installable"); %>
        <% Set updated = (Set)pageContext.findAttribute("updated"); %>
        <% if((installable != null && !installable.isEmpty()) || (updated != null && !updated.isEmpty())) { %>
          <input type="submit" name="update" value="Update Application">
        <% } %>
        <input type="submit" name="cancel" value="Cancel/Back">
      </td>
    </tr>
  </table>
</form>