<%--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="snip-wrapper">
 <s:check roles="Authenticated" permission="Edit" snip="${snip}">
  <div class="snip-title">
   <h1 class="snip-name">Documents attached to <c:out value="${snip_name}" escapeXml="false"/></h1>
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

    <form class="form" name="f" method="post" action="<c:out value='${app.configuration.path}'/>/exec/upload" enctype="multipart/form-data">
     <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
     <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
     <table class="wiki-table" border="0" cellpaddin="0" cellspacing="0">
      <tr>
       <s:check roles="Authenticated"><th><input id="all" type="checkbox" name="allChecked" onClick="return checkAll(this);"></th></s:check>
       <th>File Name</th><th>Size</th><th>Date</th><th>Type</th>
      <tr>
      <c:forEach items="${snip.attachments.all}" var="attachment" varStatus="status" >
       <tr>
        <s:check roles="Authenticated"><td><input id="file<c:out value='${status.index}'/>" type="checkbox" name="attfile" value="<c:out value='${attachment.name}'/>"/></td></s:check>
        <td><a href="<c:out value='${app.configuration.path}/space/${snip.nameEncoded}/${attachment.name}'/>"><c:out value="${attachment.name}"/></a></td>
        <td><c:out value="${attachment.size}"/></td>
        <td><fmt:formatDate value="${attachment.date}"/></td>
        <td><c:out value="${attachment.contentType}"/></td>
       </tr>
      </c:forEach>
      <tr>
       <td colspan="5" class="form-buttons">
        <s:check roles="Authenticated" permission="Attach" snip="${snip}">
          <input name="file" type="file" maxlength="1000000" accept="*/*"/>
          <input value="Upload Document" name="upload" type="submit"/>
          <input id="delete" value="Delete File(s)" name="delete" type="submit"/></br>
        </s:check>
        <input value="Back to <c:out value='${snip_name}' escapeXml="false" />" name="cancel" type="submit"/>
       </td>
      </tr>
     </table>
    </div>
   </form>
  </div>
 </s:check>
 <s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <a href="<c:out value='${app.configuration.path}'/>/exec/login.jsp">Please login to attach files!</a>
 </s:check>
</div>