<%--
  ** Snip version display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="snip-wrapper">
  <%-- include snip header and content --%>
  <div class="snip-title">
    <h1 class="snip-name">
      <fmt:message key="snip.version.title">
        <fmt:param value="${version}" />
        <fmt:param value="${snip.title}" />
      </fmt:message>

      (<c:if test="${version > 1}">
        <a href="exec/version?name=<c:out value='${snip.nameEncoded}'/>&version=<c:out value="${version-1}"/>">
          <fmt:message key="snip.version"><fmt:param value="${version-1}"/></fmt:message>
        </a> &lt;
      </c:if>
      <c:if test="${version < maxVersion }">
        <a href="exec/version?name=<c:out value='${snip.nameEncoded}'/>&version=<c:out value="${version+1}"/>">
          <fmt:message key="snip.version"><fmt:param value="${version+1}"/></fmt:message>
        </a> &gt;
      </c:if>
      ... <a href="space/<c:out value='${snip.nameEncoded}'/>"><fmt:message key="snip.version">
        <fmt:param value="${maxVersion}"/>
      </fmt:message></a>)
    </h1>

    <div class="snip-info">
      <c:out value="${snip.modified}" escapeXml="false"/>.
      <fmt:message key="snip.viewed">
        <fmt:param value="${snip.access.viewCount}"/>
      </fmt:message>
      <fmt:message key="snip.version">
        <fmt:param value="${versionSnip.version}"/>
      </fmt:message>
    </div>
  </div>
  <%-- Snip content --%>
  <div class="snip-content">
    <c:out value="${versionSnip.XMLContent}" escapeXml="false" />
  </div>
</div>
