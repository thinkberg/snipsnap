 <%--
  ** Main layout template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ page import="org.snipsnap.snip.SnipSpace,
                 org.snipsnap.app.Application,
                 org.snipsnap.snip.SnipSpaceFactory,
                 org.snipsnap.container.Components,
                 org.snipsnap.snip.Snip"%>
<%@ page pageEncoding="iso-8859-1" %>
<% response.setContentType("text/html; charset="+Application.get().getConfiguration().getEncoding()); %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="<c:out value='${app.configuration.locale}'/>" xml:lang="<c:out value='${app.configuration.locale}'/>">
 <head>
  <!-- base of this document to make all links relative -->
  <base href="<c:out value='${app.configuration.url}/'/>"/>
  <!-- content type and generator -->
  <meta http-equiv="Content-Type" content="text/html; charset=<c:out value='${app.configuration.encoding}'/>"/>
  <meta http-equiv="Generator" content="SnipSnap/<c:out value="${app.configuration.version}"/>"/>
  <s:geoUrl/>
  <!-- aggregrator related info -->
  <link rel="EditURI" type="application/rsd+xml" title="RSD" href="<c:out value='${app.configuration.url}/exec/rsd'/>"/>
  <link rel="alternate" type="application/rss+xml" title="RSS" href="<c:out value='${app.configuration.url}/exec/rss'/>"/>
  <link rel="index" href="<c:out value='${app.configuration.url}/space/snipsnap-index'/>"/>
  <!-- icons and stylesheet -->
  <link rel="shortcut icon" href="<c:out value='${app.configuration.url}/favicon.ico'/>"/>
  <link rel="icon" href="<c:out value='${app.configuration.url}/favicon.ico'/>"/>
  <link rel="STYLESHEET" type="text/css" href="<c:out value='${app.configuration.url}/theme/default.css'/>" />
  <link rel="STYLESHEET" type="text/css" href="<c:out value='${app.configuration.url}/theme/print.css'/>" media="print" />
  <!-- title of this document -->
  <title><c:out value="${app.configuration.name}" default="SnipSnap"/> :: <c:out value="${snip.name}"/></title>
 </head>
 <body>
  <div id="page-logo">
   <c:choose>
    <c:when test="${snip.name==app.configuration.startSnip && not(empty app.configuration.logo)}"><s:image root="SnipSnap/config" name="${app.configuration.logo}" alt="${app.configuration.name}"/></c:when>
    <c:when test="${snip.name!=app.configuration.startSnip && not(empty app.configuration.logo)}"><a href="<c:out value='${app.configuration.url}'/>" accesskey="1"><s:image root="SnipSnap/config" name="${app.configuration.logo}" alt="${app.configuration.name}"/></a></c:when>
    <c:when test="${snip.name==app.configuration.startSnip && empty app.configuration.logo}"><c:out value="${app.configuration.name}" default="SnipSnap"/></c:when>
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
   <%
     SnipSpace space = (SnipSpace)Components.getComponent(SnipSpace.class);
     for(int i = 1; space.exists("snipsnap-portlet-"+i) || space.exists("SnipSnap/portlet/"+i); i++) {
       Snip snip = space.load("snipsnap-portlet-"+i);
       if(null == snip) {
         snip = space.load("SnipSnap/portlet/" + i);
       }
       pageContext.setAttribute("portlet", snip);
   %>
    <div id="page-portlet-<%=i%>-wrapper">
     <div id="page-portlet-<%=i%>"><s:snip snip="${portlet}"/></div>
    </div>
   <% } %>
  </div>
  <div id="page-bottom"><s:snip name="snipsnap-copyright"/></div>
 </body>
</html>