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
  <% for(int i = ((Integer)pageContext.findAttribute("startIndex")).intValue(); i < hits.length(); i++) { %>
    <a href="../space/<%= URLEncoder.encode(hits.doc(i).get("title")) %>"><%= hits.doc(i).get("title") %></a><%= i < hits.length()-1 ? ", " : "" %>
  <% } %>
<% } else { %>
  <b>No search results.</b>
<% } %>