<%@ page pageEncoding="iso-8859-1" %><%--
--%><%@ page contentType="text/xml; charset=UTF-8"%><%--
--%><%@ page import="org.snipsnap.snip.SnipSpace,
                     org.snipsnap.snip.SnipSpaceFactory"%><%--
--%><% response.setHeader("ETag", SnipSpaceFactory.getInstance().getETag()); %><%--
--%><?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%-- hack to remove linefeed
--%><%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %><%--
--%><!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/>" -->
<rss version="2.0"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"
    xmlns:admin="http://webns.net/mvcb/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:content="http://purl.org/rss/1.0/modules/content/"
    xmlns:blogChannel="http://backend.userland.com/blogChannelModule" >
<%--
  ** RSS2 display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>
  <channel>
    <title><c:out value="${config.name}"/></title>
    <%-- usually points to "start" --%>
    <link><c:out value="${url}/${snip.nameEncoded}"/></link>
    <description><c:out value="${config.tagline}"/></description>
    <s:dublinCore snip="${snip}" format="xml"/>
    <!-- <blogChannel:changes>http://www.weblogs.com/rssUpdates/changes.xml</changes> -->
    <admin:generatorAgent rdf:resource="http://www.snipsnap.org/space/version-<c:out value='${config.version}'/>"/>
    <c:forEach items="${rsssnips}" var="child">
       <item>
        <title><c:out value="${child.name}"/></title>
        <link><c:out value="${url}/${child.nameEncoded}"/></link>
        <description><s:content snip="${child}" removeHtml="true"/></description>
        <guid isPermaLink="true"><c:out value="${url}/${child.nameEncoded}"/></guid>
        <content:encoded><s:content snip="${child}" encode="true"/></content:encoded>
        <s:dublinCore snip="${child}" format="xml"/>
        <comments><c:out value="${child.comments.postUrl}"/></comments>
      </item>
    </c:forEach>
  </channel>
</rss>