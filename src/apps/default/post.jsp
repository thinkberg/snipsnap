<%@ page import="org.radeox.util.Encoder"%>
 <%--
  ** weblog post template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name"><fmt:message key="weblog.post.title"/> <c:out value="${snip.name}"/></h1></div>
 <div class="snip-content">
  <s:check name="${param.snip}" roles="Owner:Editor">
  <c:if test="${not empty preview}">
   <div class="preview"><div class="snip-content"><c:out value="${preview}" escapeXml="false"/></div></div>
  </c:if>
   <form class="form" method="post" action="exec/storepost" enctype="multipart/form-data">
    <table>
     <tr><td><fmt:message key="weblog.post.entry.title"/><br/><input name="title" value="<c:out value="${title}" escapeXml="false"/>"/></td></tr>
     <tr><td><textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}" escapeXml="true"/></textarea></td></tr>
     <tr><td class="form-buttons">
      <input value="<fmt:message key="dialog.preview"/>" name="preview" type="submit"/>
      <input value="<fmt:message key="weblog.post"/>" name="save" type="submit"/>
      <input value="<fmt:message key="dialog.cancel"/>" name="cancel" type="submit"/>
     </td></tr>
    </table>
    <input name="name" type="hidden" value="<c:out value='${param.name}'/>"/>
    <input name="post" type="hidden" value="weblog"/>
    <input name="referer" type="hidden" value="<%= Encoder.escape(request.getHeader("REFERER")) %>"/>
   </form>
  </s:check>
  <s:check roles="Owner:Editor" invert="true">
   <fmt:message key="login.please">
     <fmt:param><fmt:message key="weblog.post.as.editor"/></fmt:param>
   </fmt:message>
  </s:check>
 </div>
</div>
