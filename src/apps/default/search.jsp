<%@ page import="org.apache.lucene.search.Hits,
                 java.net.URLEncoder"%>
<%--
  ** Search the snip space.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<h1 class="header">Search Results: <c:out value="${query}"/></h1>

<% Hits hits = (Hits)pageContext.findAttribute("hits"); %>

<% if(hits.length() > 0) { %>
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="snip-table-header"><td>Name</td><td>Score</td></tr>
    <% for(int i = ((Integer)pageContext.findAttribute("startIndex")).intValue(); i < hits.length(); i++) { %>
      <tr class="<%= i % 2 == 0 ? "snip-table-even" : "snip-table-odd" %>">
        <td width="100%"><%-- TODO 1.4 --%>
          <a href="../space/<%= URLEncoder.encode(hits.doc(i).get("title")) %>"><%= hits.doc(i).get("title") %></a>
        </td>
        <td align="right" width="100">
          <hr align="right" style="background-color: #cccccc; border: 1px solid #aaaaaa" size="10" width="<%= hits.score(i)*100 %>">
        </td>
      </tr>
    <% } %>
  </table>
<% } else { %>
  <b>No search results.</b>
<% } %>