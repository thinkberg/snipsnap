<!--
  ** Snip display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<jsp:useBean id="snip" scope="request" class="com.neotis.jsp.SnipBean" >
  <jsp:setProperty name="snip" property="*"/>
</jsp:useBean>

<table width="100%" border="0" cellspacing="2" cellpadding="1">
   <tr><td><span class="snip-name"><jsp:getProperty name="snip" property="name" /></span></td></tr>
   <s:checkRoles roles="Authenticated">
     <tr><td>[<a href="/exec/edit?name=<%= snip.getName() %>">edit</a>]</td></tr>
   </s:checkRoles>
   <s:checkRoles roles="Authenticated" invert="true">
     <tr><td><span class="inactive">[edit]</span></td></tr>
   </s:checkRoles>
 <% if (! "start".equals(snip.getName())) { %>
   <tr width="100%"><td><span class="snip-modified"><%= snip.getModified() %></span></td></tr>
 <% } %>
 <tr>
  <td width="100%">
   <jsp:getProperty name="snip" property="XMLContent" />
  </td>
  </tr>
  <tr><td>
   <!-- do not display comments on start page, only on posted
        entries -->
 <% if (! "start".equals(snip.getName())) { %>
    <jsp:getProperty name="snip" property="comments" /> |
    <a href="/exec/post?name=<%= snip.getName() %>">post comment</a>
   <% } %>
  </td></tr>
  <tr>
  <td>Referrer: <%=request.getHeader("REFERER")%></td>
 </tr>
</table>
