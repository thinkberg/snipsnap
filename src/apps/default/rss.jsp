<?xml version="1.0"?>
<!-- RSS generation done by SnipSnap -->
<rss version="0.92">

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>
<%@ page contentType="text/xml"%>

<%--
  ** RSS display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

  <channel>
    <title><c:out value="${config.name}"/></title>
    <!-- config needs a tagline -->
    <description><c:out value="${config.name}"/></description>
    <!-- usually points to "start" -->
    <link><c:out value="${url}/${snip.name}"/></link>

    <c:forEach items="${snip.childrenDateOrder}" var="child">
       <item>
        <!-- get from Snip -->
        <title><c:out value="${child.name}"/></title>
        <!-- get from Snip, external Link needed -->
        <link><c:out value="${url}/${child.name}"/></link>
      </item>
    </c:forEach>
  </channel>
</rss>