<%@ page import="org.snipsnap.snip.SnipSpace"%><%--
--%><?xml version="1.0" encoding="utf-8"?>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%-- hack to remove linefeed
--%><%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %><%--
--%><%@ page contentType="text/xml"%><%--
--%><!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/>" -->
<rsd version="0.2">
<service>
<title><c:out value="${config.name}"/></title>
<link><c:out value="${url}/${snip.nameEncoded}"/></link>
<description><c:out value="${config.tagLine}"/></description>
<docs>http://snipsnap.org/space/snipsnap-xmlrpc</docs>
<settings>
  <struct>
	<member>
    	<name>API</name>
		<value>Blogger API</value>
	</member>
  </struct>
  <url><c:out value="${baseurl}"/></url>
  <siteidentifier></siteidentifier>
  <pathtoservice>/RPC2</pathtoservice>
  <notes>SnipSnap will support other SOAP and XML-RPC interfaces in the future, see http://snipsnap.org/space/Interfaces.</notes>
</settings>
</service>
</rsd>