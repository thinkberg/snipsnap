<%@ page import="org.snipsnap.snip.SnipSpace"%><%--
--%><?xml version="1.0" encoding="utf-8"?>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%-- hack to remove linefeed
--%><%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %><%--
--%><%@ page contentType="text/xml"%><%--
--%><!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/>" -->
<rsd version="0.6" xmlns:rsd="http://archipelago.phrasewise.com/rsd" >
<service>
  <engineName>SnipSnap</engineName>
  <engineLink>http://snipsnap.org</engineLink>
  <homePageLink><c:out value="${url}/${snip.nameEncoded}"/></homePageLink>
  <apis>
   <api name="Blogger" preferred="true" rpcLink="<c:out value="${baseurl}"/>/RPC2" blogID="">
   <settings>
    <docs>http://api.blogger.com/api/</docs>
    <notes>SnipSnap partially supports Blogger API. SnipSnap will support other XML-RPC and SOAP APIs in the future.</notes>
  </settings>
  </apis>
</service>
</rsd>