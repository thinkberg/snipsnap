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
   <table width="800" border="0" cellpadding="2" cellspacing="5">
   <tr bgcolor="#333333">
     <td valign="top"><h1 style="color: #aaaaaa">SnipSnap Admin</h1></td>
   </tr>
   <tr>
     <td><jsp:include page="/menu.jsp" flush="true"/></td>
   </tr>
   <tr><td valign="top">
   <table border="0" cellpadding="0" cellspacing="1">
    <tr>
     <td valign="top" width="100%">
      <jsp:include page="<%=request.getAttribute(\"page\")%>" flush="true"/>
     </td>
    </tr>
   </table>
   </td></tr></table>
 </body>
</html>


