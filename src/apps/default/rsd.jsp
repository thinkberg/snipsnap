<%@ page pageEncoding="iso-8859-1" %><%--
--%><%@ page contentType="text/xml; charset=UTF-8"%><%--
--%><%@ page import="org.snipsnap.snip.SnipSpace"%><%--
--%><?xml version="1.0" encoding="utf-8"?>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%-- hack to remove linefeed
--%><%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %><%--
--%><!-- name="generator" content="SnipSnap/<c:out value="${config.version}"/>" -->
<rsd version="1.0" xmlns="http://archipelago.phrasewise.com/rsd" >
<service>
  <engineName>SnipSnap</engineName>
  <engineLink>http://snipsnap.org</engineLink>
  <homePageLink><c:out value="${url}/${snip.nameEncoded}"/></homePageLink>
  <apis>
   <api name="Blogger" preferred="true" apiLink="<c:out value="${baseurl}"/>/RPC2" blogID="">
   <settings>
    <docs>http://api.blogger.com/api/</docs>
    <notes>SnipSnap partially supports Blogger API. SnipSnap will support other XML-RPC and SOAP APIs in the future.</notes>
  </settings>
  </api>
  </apis>
</service>
</rsd>