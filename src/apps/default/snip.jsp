<jsp:useBean id="snip" scope="request" class="com.neotis.jsp.SnipBean" >
  <jsp:setProperty name="snip" property="*"/>
</jsp:useBean>

<table border="0" cellspacing="2" cellpadding="1">
 <tr><td class="header"><jsp:getProperty name="snip" property="name" /></td></tr>
 <tr>
  <td>
   <jsp:getProperty name="snip" property="XMLContent" />
  </td>
  <td>Referrer: <%=request.getHeader("REFERER")%></td>

 </tr>
</table>



