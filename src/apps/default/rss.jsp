<?xml version="1.0"?>
<!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/> -->
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
    <description><c:out value="${config.tagLine}"/></description>
    <%-- usually points to "start" --%>
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