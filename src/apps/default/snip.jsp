<!--
  ** Snip display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<jsp:useBean id="snip" scope="request" class="com.neotis.jsp.SnipBean" >
  <jsp:setProperty name="snip" property="*"/>
</jsp:useBean>
<jsp:useBean id="user" scope="request" class="com.neotis.jsp.UserBean" >
  <jsp:setProperty name="user" property="session" value="<%= session %>"/>
</jsp:useBean>


<table width="100%" border="0" cellspacing="2" cellpadding="1">
 <tr><td><span class="snip-name"><jsp:getProperty name="snip" property="name" /></span></td></tr>
 <% if(user.isAuthenticated()) { %>
  <tr><td>[<a href="/exec/edit?name=<%= snip.getName() %>">edit</a>]</td></tr>
 <% } else { %>
  <tr><td><span class="inactive">[edit]</span></td></tr>
 <% } %>
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
    <jsp:getProperty name="snip" property="comments" /> |
    <a href="/exec/post?name=<%= snip.getName() %>">post comment</a>
   <% } %>
  </td></tr>
  <tr>
  <td>Referrer: <%=request.getHeader("REFERER")%></td>
 </tr>
</table>



