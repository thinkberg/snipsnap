<jsp:useBean id="snip" scope="request" class="com.neotis.jsp.SnipBean" >
  <jsp:setProperty name="snip" property="*"/>
</jsp:useBean>

<table width="100%" border="0" cellspacing="2" cellpadding="1">
 <tr><td><span class="snip-name"><jsp:getProperty name="snip" property="name" /></span></td></tr>
 <tr><td>[<a href="/exec/edit?name=<%= snip.getName() %>">edit</a>]</td></tr>
 <tr width="100%"><td><%= snip.getModified() %></td></tr>
 <tr>
  <td width="100%">
   <jsp:getProperty name="snip" property="XMLContent" />
  </td>
  </tr>
  <tr><td>
   <!-- do not display comments on start page, only on posted
        entries -->
   <% if (! snip.getName().equals("start")) { %>
    <jsp:getProperty name="snip" property="comments" />
   <% } %>
  </td></tr>
  <tr>
  <td>Referrer: <%=request.getHeader("REFERER")%></td>
 </tr>
</table>



