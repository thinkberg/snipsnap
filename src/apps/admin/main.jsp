<!--
  ** Admin main page.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>SnipSnap Admin</title>
    <link type="text/css" href="../default.css" rel="STYLESHEET"/>
  </head>
  <body>
    <table width="800" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top" width="100%">
          <img src="../images/snip.png"/>
        </td>
      </tr>
      <tr><td width="100%" class="menu"><c:import url="/menu.jsp"/></td></tr>
      <tr><td valign="top">
        <table width="100%"border="0" cellpadding="8" cellspacing="0">
          <tr>
            <td valign="top" width="100%">
              <c:import url="${page}"/>
            </td>
          </tr>
        </table>
      </td></tr>
    </table>
  </body>
</html>


