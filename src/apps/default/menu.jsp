<%@ page import="org.snipsnap.date.Month,
                 org.snipsnap.snip.Snip,
                 org.snipsnap.snip.SnipSpace,
                 org.snipsnap.app.Application"%>
 <%--
  ** Menu template
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<table class="menu" width="200" border="0" cellpadding="4" cellspacing="1">
 <tr><td>
   <form method="get" action="../space/snipsnap-search">
     <input type="text" size="18" name="query" style="border: 1px solid #aaaaaa"/> <input type="submit" name="search" value="search"/>
   </form>
 </td></tr>
 <tr><td>
   <s:snip load="snipsnap-intro" id="intro"/>
   <c:out value="${intro.XMLContent}" escapeXml="false"/>
 </td></tr>
 <tr><td>
  <b>Users:</b><br/>
  <% pageContext.setAttribute("users", Application.getCurrentUsers()); %>
  <% pageContext.setAttribute("guests", new Integer(Application.getGuestCount())); %>
  <c:forEach var="user" items="${users}">
   <a href="<c:url value='/space/${user.name}'/>"><c:out value="${user.name}"/></a><br/>
  </c:forEach>
  ... and <c:out value="${guests}"/> Guests.
 </td></tr>
 <tr><td>
  <b>Recent Changes:</b><br/>
  <%-- replace this with a JSTL tag ala  s:recent/> --%>
  <c:forEach var="snip" items="${space.changed}">
   <a href="<c:url value='/space/${snip.nameEncoded}'/>"><c:out value="${snip.name}"/></a><br/>
  </c:forEach>
 </td></tr>
 <tr><td>
  <%-- replace this with a JSTL tag ala  s:calendar/> --%>
  <% Month m = new Month(); %>
  <%= m.getView() %>
  </td></tr>
  <tr><td>
   <s:snip load="snipsnap-blogrolling" id="blogrolling"/>
   <c:out value="${blogrolling.XMLContent}" escapeXml="false"/>
 </td></tr>
 <tr><td>
   <a href="http://validator.w3.org/check/referer"><s:image name="valid-xhtml10"/></a> <s:image name="vcss"/>
 </td></tr>
 <tr><td>
   <a href="/exec/rss"><s:image name="xml-rss"/></a>
 </td></tr>
</table>