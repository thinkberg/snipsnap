 <%--
  ** Main layout template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ page import="org.snipsnap.snip.SnipSpace,
                 org.snipsnap.app.Application,
                 org.snipsnap.snip.SnipSpaceFactory"%>
<%@ page pageEncoding="iso-8859-1" %>
<% response.setContentType("text/html; charset="+Application.get().getConfiguration().getEncoding()); %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="<c:out value='${app.configuration.locale}'/>" xml:lang="<c:out value='${app.configuration.locale}'/>">
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=<c:out value='${app.configuration.encoding}'/>"/>
  <meta http-equiv="Generator" content="SnipSnap/<c:out value="${app.configuration.version}"/>"/>
  <link rel="EditURI" type="application/rsd+xml" title="RSD" href="<c:out value='${app.configuration.url}/exec/rsd'/>"/>
  <link rel="alternate" type="application/rss+xml" title="RSS" href="<c:out value='${app.configuration.url}/exec/rss'/>"/>
  <link rel="index" href="<c:out value='${app.configuration.url}/space/snipsnap-index'/>"/>
  <link rel="shortcut icon" href="<c:out value='${app.configuration.url}/favicon.ico'/>">
  <link rel="icon" href="<c:out value='${app.configuration.url}/favicon.ico'/>">
  <s:geoUrl/>
  <s:dublinCore snip="${snip}"/>
  <title><c:out value="${app.configuration.name}" default="SnipSnap"/> :: <c:out value="${snip.name}" default=""/></title>
  <link type="text/css" href="<c:out value='${app.configuration.path}/default.css'/>" rel="STYLESHEET"/>
  <link type="text/css" href="<c:out value='${app.configuration.path}/print.css'/>" media="print" rel="STYLESHEET"/>
 </head>
 <body>
  <div id="page-logo">
   <c:choose>
    <c:when test="${snip.name=='start' && not(empty app.configuration.logo)}"><s:image name="${app.configuration.logo}" alt="${app.configuration.name}"/></c:when>
    <c:when test="${snip.name=='start' && empty app.configuration.logo}"><c:out value="${app.configuration.name}" default="SnipSnap"/></c:when>
    <c:when test="${snip.name!='start' && not(empty app.configuration.logo)}"><a href="<c:out value='${app.configuration.url}'/>" accesskey="1"><s:image name="${app.configuration.logo}" alt="${app.configuration.name}"/></a></c:when>
    <c:otherwise><a href="<c:out value='${app.configuration.url}'/>" accesskey="1"><c:out value="${app.configuration.name}" default="SnipSnap"/></a></c:otherwise>
   </c:choose>
  </div>
  <div id="page-title">
   <div id="page-tagline"><c:out value="${app.configuration.tagline}"/></div>
   <div id="page-buttons"><c:import url="util/mainbuttons.jsp"/></div>
  </div>
  <div id="page-wrapper">
   <div id="page-content">
    <c:import url="${page}"/>
    <s:debug/>
   </div>
   <% for(int i = 1; SnipSpaceFactory.getInstance().exists("snipsnap-portlet-"+i); i++) { %>
    <% pageContext.setAttribute("snip", SnipSpaceFactory.getInstance().load("snipsnap-portlet-"+i)); %>
    <div id="page-portlet-<%=i%>"><s:snip snip="${snip}"/></div>
   <% } %>
 </div>
   <div id="page-bottom"><s:snip name="snipsnap-copyright"/></div>
 </body>
</html>


