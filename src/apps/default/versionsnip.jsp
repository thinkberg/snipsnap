<%--
  ** Snip version display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <%-- include snip header and content --%>
<div class="snip-title">
   <h1 class="snip-name">Version #<c:out value="${version}"/> of <c:out value="${snip.title}"/>
   <c:if test="${version > 1}">
   <a href="exec/version?name=<c:out value='${snip.nameEncoded}'/>&version=<c:out value="${version-1}"/>">#<c:out value="${version-1}"/></a> &lt;
   </c:if>
   <c:if test="${version < maxVersion }">
    &gt; <a href="exec/version?name=<c:out value='${snip.nameEncoded}'/>&version=<c:out value="${version+1}"/>">#<c:out value="${version+1}"/></a>
   </c:if>
   ... #<c:out value="${maxVersion}"/></h1>
   <div class="snip-info"><c:out value="${snip.modified}" escapeXml="false"/> Viewed <c:out value="${snip.access.viewCount}"/> times. #<c:out value="${versionSnip.version}"/></div>
</div>
<%-- Snip content --%>
<div class="snip-content">
  <div class="snip-meta">
   <c:out value="${versionSnip.XMLContent}" escapeXml="false" />
  </div>
</div>
</div>
