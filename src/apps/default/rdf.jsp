<?xml version="1.0" encoding="utf-8"?>
<rdf:RDF
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns="http://purl.org/rss/1.0/"
>
<!-- RSS generation done by SnipSnap -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>
<%@ page contentType="text/xml"%>

<%--
  ** RSS display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

  <channel rdf:about="http://www.snipsnap.org/rss">
    <title><c:out value="${config.name}"/></title>
    <!-- config needs a tagline -->
    <description><c:out value="${config.name}"/></description>
    <link><c:out value="${url}"/></link>

    <items>
     <rdf:Seq>
       <rdf:li rdf:resource="http://www.snipsnap.org/space/item" />
     </rdf:Seq>
    </items>
  </channel>

  <c:forEach items="${snip.childrenDateOrder}" var="child">
     <item rdf:about='<c:out value="${url}"/>/<c:out value="${child.name}"/>'>
      <!-- get from Snip -->
      <title><c:out value="${child.name}"/></title>
      <!-- get from Snip, external Link needed -->
      <link><c:out value="${url}"/><c:out value="${child.name}"/></link>
    </item>
  </c:forEach>

</rdf:RDF>