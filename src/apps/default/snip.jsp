<%--
  ** Snip display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div id="snip-wrapper">
 <%-- include snip header and content --%>
 <c:import url="util/snip-base.jsp"/>
 <%-- Snip "post comment" string --%>
 <div id="snip-post-comments">
  <c:if test="${snip.notWeblog}">
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
  </c:if>
 </div>
 <div id="snip-sniplinks"><s:snipLinks snip="${snip}" width="4" start="#ffffff" end="#b0b0b0"/></div>
 <div id="snip-backlinks"><s:backLinks snip="${snip}" count="15"/></div>
</div>
