<jsp:useBean id="snip" scope="request" class="com.neotis.jsp.SnipBean" >
  <jsp:setProperty name="snip" property="request" value="<%= request %>"/>
</jsp:useBean>

<table border="0" cellspacing="2" cellpadding="1">
 <tr>
  <td>
   <jsp:getProperty name="snip" property="content" />
  </td>
 </tr>
</table>



