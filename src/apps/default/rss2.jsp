<?xml version="1.0" encoding="utf-8"?>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%-- hack to remove linefeed
--%><%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %><%--
--%><%@ page contentType="text/xml"%><%--
--%><!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/> -->
<rss version="2.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"
    xmlns:admin="http://webns.net/mvcb/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:content="http://purl.org/rss/1.0/modules/content/">
  >
<%--
  ** RSS2 display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>
  <channel>
    <title><c:out value="${config.name}"/></title>
    <%-- usually points to "start" --%>
    <link><c:out value="${url}/${snip.nameEncoded}"/></link>
    <description><c:out value="${config.tagLine}"/></description>
    <s:dublinCore snip="${snip}" format="xml"/>
    <admin:generatorAgent rdf:resource="http://www.snipsnap.org/?v=<c:out value='${config.version}'/>"/>

    <c:forEach items="${snip.childrenDateOrder}" var="child">
       <item>
        <title><c:out value="${child.name}"/></title>
        <link><c:out value="${url}/${child.nameEncoded}"/></link>
      </item>
    </c:forEach>
  </channel>
</rss>