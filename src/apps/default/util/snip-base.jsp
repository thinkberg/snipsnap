<%--
  ** Displays the snip content if it's not a weblog and the header
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<%-- Snip header, displayed only when snip is not a weblog --%>
<c:if test="${snip.notWeblog}">
 <div class="snip-title">
  <h1 class="snip-name"><c:out value="${snip.name}"/>
  <c:if test="${snip.comment}">
   <span class="snip-commented-snip"><s:image name="commented"/> <a href="../comments/<c:out value='${snip.commentedSnip.nameEncoded}'/>"><c:out value='${snip.commentedSnip.name}'/></a></span>
  </c:if>
  </h1>
  <div class="snip-info"><c:out value="${snip.modified}" escapeXml="false"/> Viewed <c:out value="${snip.access.viewCount}"/> times.</div>
  <div class="snip-buttons"><c:import url="util/buttons.jsp"/></div>
 </div>
</c:if>
<%-- Snip content --%>
<div class="snip-content">
 <%-- <div class="snip-label">[[<c:forEach items="${snip.labels.ids}" var="label"><c:out value="label"/></c:forEach>]]</div> --%>
 <c:out value="${snip.XMLContent}" escapeXml="false" />
 <c:out value="${snip.attachments.listString}" escapeXml="false" />
</div>