<!--
  ** Snip display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<table width="100%" border="0" cellspacing="2" cellpadding="1">
 <!-- do not display header on weblogs -->
 <c:if test="${snip.notWeblog}">
   <tr><td><span class="snip-name"><c:out value="${snip.name}"/></span></td></tr>
   <s:check roles="Authenticated" permission="Edit" snip="${snip}">
     <tr><td>[<a href="/exec/edit?name=<c:out value='${snip.name}'/>">edit</a>]</td></tr>
   </s:check>
   <s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
     <tr><td><span class="inactive">[edit]</span></td></tr>
   </s:check>
   <tr width="100%"><td><span class="snip-modified"><c:out value="${snip.modified}" escapeXml="false"/></span></td></tr>
 </c:if>
 <tr>
  <td width="100%">
   <c:out value="${snip.XMLContent}" escapeXml="false" />
  </td>
  </tr>
  <tr><td>
   <!-- do not display comments on start page, only on posted
        entries -->
    <c:if test="${snip.notWeblog}">
      <c:out value="${snip.comments}" escapeXml="false" /> |
    </c:if>
  </td></tr>
  <tr>
  <td>Referrer: <%=request.getHeader("REFERER")%></td>
 </tr>
</table>
