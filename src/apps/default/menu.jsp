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
   <form method="POST" action="../exec/search">
     <input type="text" size="18" name="query" style="border: 1px solid #aaaaaa"> <input type="submit" name="search" value="search">
   </form>
 </tr></td>
 <tr><td></td></tr>
 <tr><td>
   <s:snip load="snipsnap-intro" id="intro"/>
   <c:out value="${intro.XMLContent}" escapeXml="false"/>
 </td></tr>
 <tr><td>
  </b>Also here are</b>:<br/>
  <% pageContext.setAttribute("users", Application.getCurrentUsers()); %>
  <c:forEach var="user" items="${users}">
   <a href="../space/<c:out value='${user.name}'/>"><c:out value="${user.name}"/></a><br/>
  </c:forEach>
 </td></tr>
 <tr><td>
  <b>Recent Changes:</b><br>
  <%-- replace this with a JSTL tag ala  s:recent/> --%>
  <c:forEach var="snip" items="${space.changed}">
   <a href="../space/<c:out value='${snip.nameEncoded}'/>"><c:out value="${snip.name}"/></a><br/>
  </c:forEach>
 </td></tr>
 <tr><td>
  <p>
  <%-- replace this with a JSTL tag ala  s:calendar/> --%>
  <% Month m = new Month(); %>
  <%= m.getView() %>
  </p>

  <p>
   <s:snip load="snipsnap-blogrolling" id="blogrolling"/>
   <c:out value="${blogrolling.XMLContent}" escapeXml="false"/>
  </p>
 </td></tr>
</table>