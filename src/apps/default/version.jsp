<%@ page pageEncoding="iso-8859-1" %><%--
--%><%@ page contentType="text/xml; charset=UTF-8"%><%--
--%><%@ page import="org.snipsnap.snip.SnipSpace"%><%--
--%><% response.setHeader("ETag", SnipSpace.getInstance().getETag()); %><%--
--%><?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%-- hack to remove linefeed
--%><%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %><%--
--%><!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/>" -->
<rsv version="0.1">
<%--
  ** RSV display template.
  ** Really simple version protokoll
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>
  <engineName>SnipSnap</engineName>
  <engineLink>http://snipsnap.org</engineLink>
  <engineVersion><c:out value="${config.version}"/></engineVersion>
</rsv>