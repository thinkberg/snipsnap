<%@ page pageEncoding="iso-8859-1" %><%--
--%><%@ page contentType="text/xml; charset=UTF-8"%><%--
--%><?xml version="1.0" encoding="utf-8"?>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%-- hack to remove linefeed
--%><%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %><%--
--%><!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/> -->
<rss version="0.92">
<%--
  ** RSS display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>
  <channel>
    <title><c:out value="${config.name}" escapeXml="true"/></title>
    <description><c:out value="${config.tagline}" escapeXml="true"/></description>
    <%-- usually points to "start" --%>
    <link><c:out value="${url}/${snip.nameEncoded}"/></link>
    <c:forEach items="${snip.childrenDateOrder}" var="child">
       <item>
        <title><c:out value="${child.name}" escapeXml="true"/></title>
        <link><c:out value="${url}/${child.nameEncoded}"/></link>
      </item>
    </c:forEach>
  </channel>
</rss>