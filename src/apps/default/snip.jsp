<%--
  ** Snip display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <%-- include snip header and content --%>
 <c:import url="util/snip-base.jsp"/>
 <c:if test="${snip.notWeblog}">
  <%-- Snip "post comment" string --%>
  <div class="snip-post-comments">
   <c:choose>
    <c:when test="${snip.comment}">
      <c:out value="${snip.commentedSnip.comments}" escapeXml="false" /> |
      <c:out value="${snip.commentedSnip.comments.postString}" escapeXml="false" />
    </c:when>
    <c:otherwise>
      <c:out value="${snip.comments}" escapeXml="false" /> |
      <c:out value="${snip.comments.postString}" escapeXml="false" />
    </c:otherwise>
   </c:choose>
  </div>
 </c:if>
 <c:if test="${app.configuration.featureSniplinksShow == 'true'}">
   <div class="snip-sniplinks"><s:snipLinks snip="${snip}" width="4" start="#ffffff" end="#b0b0b0"/></div>
 </c:if>
 <c:if test="${app.configuration.featureReferrerShow == 'true'}">
   <div class="snip-backlinks"><s:backLinks snip="${snip}" count="15"/></div>
 </c:if>
</div>
