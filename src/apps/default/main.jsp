<%@ page import="org.snipsnap.snip.SnipSpace"%>
 <%--
  ** Main layout template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="<c:out value='${app.configuration.locale}'/>" xml:lang="<c:out value='${app.configuration.locale}'/>">
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
  <link rel="alternate" type="application/rss+xml" title="RSS"
      href="<c:out value='${app.configuration.url}/exec/rss'/>"/>
  <link rel="index" href="snipsnap-index"/>
  <s:dublinCore snip="${snip}"/>
  <title><c:out value="${app.configuration.name}" default="SnipSnap"/> :: <c:out value="${snip.name}" default=""/></title>
  <link type="text/css" href="<c:url value='/default.css'/>" rel="STYLESHEET"/>
  <link type="text/css" href="<c:url value='/print.css'/>" media="print" rel="STYLESHEET"/>
 </head>
 <body>
  <div id="page-logo">
   <c:choose>
    <c:when test="${snip.name=='start'}"><s:image name="snip"/></c:when>
    <c:otherwise><a href="<c:out value='${app.configuration.url}'/>" accesskey="1"><s:image name="snip"/></a></c:otherwise>
   </c:choose>
  </div>
  <div id="page-title">
   <div id="page-tagline"><c:out value="${app.configuration.tagLine}"/></div>
   <div id="page-buttons"><c:import url="util/mainbuttons.jsp"/></div>
  </div>
  <div id="page-wrapper">
   <div id="page-content">
    <c:import url="${page}"/>
    <div id="page-bottom">
     <a href="<c:out value='${app.configuration.url}'/>"><c:out value="${app.configuration.name}"/></a> | <span id="copyright"></span><br/>
     <a href="http://snipsnap.org/"><s:image name="logo_small"/></a>
    </div>
    <s:debug/>
   </div>
   <% for(int i = 1; SnipSpace.getInstance().exists("snipsnap-portlet-"+i); i++) { %>
    <% pageContext.setAttribute("snip", SnipSpace.getInstance().load("snipsnap-portlet-"+i)); %>
    <div id="page-portlet-<%=i%>"><s:snip snip="${snip}"/></div>
   <% } %>
   </c:if>
  </div>
 </body>
</html>


