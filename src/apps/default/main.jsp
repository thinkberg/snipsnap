<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Weblog/Wiki</title>
  <link type="text/css" href="/default.css" rel="STYLESHEET"/>
 </head>
 <body>
   <h1 class="title">Weblog: <%= request.getAttribute("path") %></h1>
   <table border="0" cellpadding="4" cellspacing="1">
    <tr>
     <td valign="top">
      <jsp:include page="/menu.jsp" flush="true"/>
     </td>
     <td valign="top">
      <jsp:include page="<%=request.getAttribute(\"page\")%>" flush="true"/>
      </td>
    </tr>
   </table>
 </body>
</html>


