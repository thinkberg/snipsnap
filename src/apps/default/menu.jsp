<%@ page import="com.neotis.date.Month,
                 com.neotis.snip.Snip,
                 com.neotis.snip.SnipSpace"%>
 <!--
  ** Menu template
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<table class="menu" width="100%" border="0" cellpadding="4" cellspacing="1">
 <tr><td>
  <s:check roles="Authenticated">
    <a href="../exec/post.jsp">post blog</a>
  </s:check>
  </td></tr>
 <tr><td>
  <b>Recent Changes:</b><br>
  <!-- replace this with a JSTL tag ala  s:recent/> -->
  <c:forEach var="snip" items="${space.changed}">
   <a href="../space/<c:out value='${snip.name}'/>"><c:out value="${snip.name}"/></a><br/>
  </c:forEach>
 </td></tr>
 <tr><td>
  <p>
  <!-- replace this with a JSTL tag ala  s:calendar/> -->
  <% Month m = new Month(); %>
  <%= m.getView() %>
  </p>

  <p>
   <s:snip load="snipsnap-blogrolling" id="blogrolling"/>
   <c:out value="${blogrolling.XMLContent}" escapeXml="false"/>
  </p>
 </td></tr>

</table>
