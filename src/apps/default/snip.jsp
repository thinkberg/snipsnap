<%--
  ** Snip display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div id="snip-wrapper">
 <%-- Snip header, displayed only when snip is not a weblog --%>
 <c:if test="${snip.notWeblog}">
  <div id="snip-header">
   <div id="snip-title">
    <h1 class="snip-name"><c:out value="${snip.name}"/></h1>
    <c:if test="${snip.comment}">
     <span class="snip-comment"><s:image name="arrow"/> <a href="../comments/<c:out value='${snip.commentedSnip.nameEncoded}'/>"><c:out value='${snip.commentedSnip.name}'/></a></span>
    </c:if>
   </div>
   <div id="snip-buttons"><c:import url="util/buttons.jsp"/></div>
   <div id="snip-info"><c:out value="${snip.modified}" escapeXml="false"/> Viewed <c:out value="${snip.access.viewCount}"/> times.</div>
  </div>
 </c:if>
 <%-- Snip content --%>
 <div id="snip-content"><c:out value="${snip.XMLContent}" escapeXml="false" /></div>
 <%-- Snip "post comment" string --%>
 <div id="snip-comments">
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
