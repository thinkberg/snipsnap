<%--
  ** Main layout template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>SnipSnap :: <c:out value="${config.name}"/> :: Administration</title>
  <link type="text/css" href="../default.css" rel="STYLESHEET"/>
 </head>
 <body>
   <table width="800" border="0" cellpadding="0" cellspacing="0">
   <tr><td valign="top">
     <table border="0" cellpadding="4" cellspacing="1">
      <tr><td colspan="2"><a href="http://www.snipsnap.org"><s:image name="snip"/></a></td></tr>
      <tr><td colspan="2">
        <div id="Header">Bigger. Better. Faster. More.
          <c:import url="util/mainbuttons.jsp"/>
         </div>
       </td>
      </tr>
      <tr>
       <td valign="top" width="100%">
         <c:choose>
           <c:when test="${user.admin}">
             <c:import url="${page}"/>
           </c:when>
           <c:otherwise>
             You are not the administrator. Please <a href="<c:url value='/'/>">go back to <c:out value="${config.name}"/></a>!
           </c:otherwise>
         </c:choose>
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


