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
  <tr>
   <td><span class="snip-name"><c:out value="${snip.name}"/></span>
     <c:if test="${snip.comment}">
       <s:image name="arrow"/> <a href="../comments/<c:out value='${snip.commentedSnip.nameEncoded}'/>"><c:out value='${snip.commentedSnip.nameEncoded}'/></a>
     </c:if>
   </td>
   <td align="right">
     <c:import url="util/buttons.jsp"/>
   </td>
  </tr>
  <tr width="100%">
   <td colspan="2">
    <span class="snip-modified"><c:out value="${snip.modified}"
          escapeXml="false"/></span>
   </td>
  </tr>
  <tr><td colspan="2"></td></tr>
 </c:if>
 <tr>
  <td colspan="2" width="100%">
   <c:out value="${snip.XMLContent}" escapeXml="false" />
  </td>
 </tr>
 <tr><td colspan="2"></td></tr>
 </tr>
 <tr><td colspan="2">
  <!-- do not display comments on start page, only on posted
        entries -->
  <c:if test="${snip.notWeblog}">
   <c:out value="${snip.comments}" escapeXml="false" /> |
   <c:out value="${snip.comments.postString}" escapeXml="false" />
  </c:if>
 </td></tr>
 <!--
 <tr>
  <td>Referrer: <%=request.getHeader("REFERER")%></td>
 </tr>
 -->
</table>
