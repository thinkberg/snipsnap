<%@ page import="org.snipsnap.snip.Snip,
                 java.util.Collection,
                 java.util.Collections,
                 org.radeox.util.Encoder"%>
 <%--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="snip-wrapper">
 <s:check permission="EDIT_SNIP" context="${snip}">
  <div class="snip-title">
   <h1 class="snip-name">
    <fmt:message key="snip.attachments.title">
     <fmt:param value="${snip_name}"/>
    </fmt:message>
   </h1>
  </div>
  <div class="snip-content">
   <c:if test="${error != null}">
     <div class="error"><c:out value="${error}"/></div>
     <p/>
   </c:if>
   <div class="snip-input">
     <script type="text/javascript" language="Javascript">
      <!--
      function checkAll(checkbox) {
        if(checkbox.name == 'allChecked') {
          <c:forEach items="${snip.attachments.all}" var="attachment" varStatus="status" >
          document.getElementById("<c:out value="file${status.index}"/>").checked = checkbox.checked
          </c:forEach>
        }
      }
      -->
    </script>
    <form class="form" name="f" method="post" action="exec/upload" enctype="multipart/form-data">
     <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
     <input name="referer" type="hidden" value="<%= Encoder.escape(request.getHeader("REFERER")) %>"/>
     <table class="wiki-table" border="0" cellpaddin="0" cellspacing="0">
      <tr>
       <s:check permission="ADD_ATTACHMENT" context="${snip}"><th><input id="all" type="checkbox" name="allChecked" onClick="return checkAll(this);"/></th></s:check>
       <th><fmt:message key="snip.attachments.file.name"/></th>
       <th><fmt:message key="snip.attachments.file.size"/></th>
       <th><fmt:message key="snip.attachments.file.date"/></th>
       <th><fmt:message key="snip.attachments.file.type"/></th>
      </tr>
      <c:forEach items="${snip.attachments.all}" var="attachment" varStatus="status" >
       <tr>
        <s:check permission="ADD_ATTACHMENT" context="${snip}"><td><input id="file<c:out value='${status.index}'/>" type="checkbox" name="attfile" value="<c:out value='${attachment.name}' escapeXml="true"/>"/></td></s:check>
        <td><a href="space/<c:out value='${snip.nameEncoded}/${attachment.nameEncoded}'/>"><c:out value="${attachment.name}" escapeXml="true"/></a></td>
        <td><c:out value="${attachment.size}"/></td>
        <td><fmt:formatDate value="${attachment.date}"/></td>
        <td><c:out value="${attachment.contentType}" escapeXml="true"/></td>
       </tr>
      </c:forEach>
      <c:if test="${empty snip.attachments.all}">
       <tr><td colspan="5"><fmt:message key="snip.attachments.nofiles"/></td></tr>
      </c:if>
      <tr>
       <td colspan="5" class="form-buttons">
        <s:check permission="ADD_ATTACHMENT" context="${snip}"">
          <input name="file" type="file" maxlength="1000000" accept="*/*"/>
          <br/>
          <fmt:message key="snip.attachments.file.name"/>
          <input id="filename" name="filename" type="text" value="" size="10" maxlength="256"/>
          <fmt:message key="snip.attachments.file.type"/>
          <input id="mimetype" name="mimetype" type="text" value="" size="10" maxlength="256"/>
          <input value="<fmt:message key="snip.attachments.upload"/>" name="upload" type="submit"/>
          <br/>
          <input value="<fmt:message key="snip.attachments.delete"/>" name="delete" type="submit"/>
        </s:check>
        <input value="<fmt:message key="dialog.back.to"><fmt:param value="${snip.name}"/></fmt:message>" name="cancel" type="submit"/>
       </td>
      </tr>
     </table>
    </div>
   </form>
  </div>
 </s:check>
 <s:check permission="ADD_ATTACHMENT" context="${snip}">
  <fmt:message key="login.please">
   <fmt:param><fmt:message key="snip.attachments.login"/></fmt:param>
 </fmt:message>
 </s:check>
</div>