<!--
  ** Admin main page.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>SnipSnap Admin</title>
  <link type="text/css" href="/default.css" rel="STYLESHEET"/>
 </head>
 <body>
   <table width="800" border="0" cellpadding="0" cellspacing="0"><tr><td valign="top">
   <table border="0" cellpadding="4" cellspacing="1">
    <tr>
     <td valign="top">
      <h1>SnipSnap</h1>
      <jsp:include page="/menu.jsp" flush="true"/>
     </td>
     <td valign="top" width="100%">
      <jsp:include page="<%=request.getAttribute(\"page\")%>" flush="true"/> -->
     </td>
    </tr>
   </table>
   </td></tr></table>
 </body>
</html>


