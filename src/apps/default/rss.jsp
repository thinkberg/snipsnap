<%--
  ** RSS display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<%@ page contentType="text/xml"%>

<?xml version="1.0" encoding="utf-8"?>
<rdf:RDF
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns="http://purl.org/rss/1.0/"
>

<channel rdf:about="http://www.snipsnap.org/rss">
  <title>SnipSnap</title>
  <description>SnipSnap - Wikilog Software News</description>
  <link>http://www.snipsnap.org</link>

  <items>
   <rdf:Seq>
     <li rdf resource="http://www.snipsnap.org/space/item" />
   </rdf:Seq>
  </items>
</channel>

<item rdf:about="http://www.snipsnap.org/space/item">
 <!-- get from Snip -->
 <tile>2002-08-03</title>
 <!-- get from Snip, external Link needed -->
 <link>http://www.snipsnap.org/space/item</link>
</item>

</rdf:RDF>