<%--
  ** Main layout template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<!--
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    -->
<link rel="alternate" type="application/rss+xml" title="RSS" href="<c:out value='${app.configuration.url}/exec/rss'/>">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
  <title>SnipSnap :: <c:out value="${snip.name}" default=""/></title>
  <link type="text/css" href="<c:url value='/default.css'/>" rel="STYLESHEET"/>
  <script type="javascript">
  <!--
    // auto focus special forms
    function setFocus() {
      if(document.f) {
        if(document.f.elements[0]) {
          document.f.elements[0].focus();
        }
      }
    }
  -->
  </script>
 </head>
 <body onload="setFocus();">
   <table width="800" border="0" cellpadding="0" cellspacing="0"><tr><td valign="top">
   <table border="0" cellpadding="4" cellspacing="1">
    <tr><td colspan="2">
     <c:choose>
       <c:when test="${snip.name=='start'}"><s:image name="snip"/></c:when>
       <c:otherwise><a href="http://www.snipsnap.org"><s:image name="snip"/></a></c:otherwise>
     </c:choose>
    </td></tr>
    <tr><td colspan="2">
     <div id="Header"><c:out value="${app.configuration.tagLine}"/>
      <c:import url="util/mainbuttons.jsp"/>
     </div>
     </td>
    </tr>
    <tr>
     <td valign="top" width="100%">
      <c:import url="${page}"/>
      <pre>

      </pre>
      <s:debug/>
     </td>
     <td valign="top">
      <div id="Menu">
        <jsp:include page="/menu.jsp" flush="true"/>
      </div>
     </td>
    </tr>
   </table>
   </td></tr>
   <tr><td colspan="2">
   <p align="center">
   <a href="http://www.snipsnap.org/">www.snipsnap.org</a> | Copyright 2000-2002 Matthias L. Jugel, Stephan J.Schmidt<br/>
   <a href="http://www.snipsnap.org/"><s:image name="logo_small"/></a>
   </p>
   </td></tr>
   </table>
 </body>
</html>


