<%@ page pageEncoding="iso-8859-1" %><%--
--%><%@ page contentType="text/plain; charset=UTF-8"%><%--
--%><%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%-- hack to remove linefeed
--%><%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %><%--
--%><!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/> -->

<c:out value="${url}/${snip.name}"/>

<c:forEach items="${rsssnips}" var="child"><%--
  --%><c:out value="${child.name}" escapeXml="true"/>
</c:forEach>