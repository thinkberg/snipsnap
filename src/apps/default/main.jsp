<!--
  ** Main layout template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Weblog/Wiki</title>
  <link type="text/css" href="/default.css" rel="STYLESHEET"/>
 </head>
 <body>
   <table width="800" border="0" cellpadding="0" cellspacing="0"><tr><td valign="top">
   <table border="0" cellpadding="4" cellspacing="1">
    <tr><td colspan="2"><img src="/images/snip.png"/></td></tr>
    <tr><td colspan="2">
     <div id="Header">Bigger. Better. Faster. More.
      <font size="2">[ <a href="/space/start">start</a> |
       <s:check roles="Authenticated">
          logged in as <a href="/space/<c:out value='${app.user.login}'/>"><c:out value="${app.user.login}"/></a> | <a href="/exec/authenticate?logoff=true">logoff</a>
       </s:check>
       <s:check roles="Authenticated" invert="true">
        <a href="/exec/login.jsp">login</a> or <a href="/exec/register.jsp">register</a>
       </s:check> ]</font>
     </div>
     </td>
    </tr>
    <tr>
     <td valign="top" width="100%">
      <c:import url="${page}"/>
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
   Copyright 2000-2002 Matthias L. Jugel, Stephan J.Schmidt <a href="http://www.snipsnap.org">www.snipsnap.org</a>

   </p>
   </td></tr>
   </table>
 </body>
</html>


