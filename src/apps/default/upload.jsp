<%--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <s:check roles="Authenticated" permission="Edit" snip="${snip}">
  <div class="snip-title">
   <h1 class="snip-name">Upload to <c:out value="${snip_name}" escapeXml="false"/></h1>
  </div>
  <div class="snip-content">
   <c:if test="${error != null}">
     <div class="error"><c:out value="${error}"/></div>
     <p/>
   </c:if>
   <div class="snip-input">
    <form class="form" name="f" method="post" action="../exec/upload" enctype="multipart/form-data">
     <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
     <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
     <span class="heading-1">Attached Documents</span>
     <table border="0" cellpaddin="0" cellspacing="0">
      <th><td>File Name<td><td>Size<td><td>Date</td><th>
      <c:forEach items="${attachments}" var="name">
       <tr>
        <td><c:out value="${name}"/></td>
        <td><c:out value="${atts[name].size}"/></td>
        <td><c:out value="${atts[name].date}"/></td>
        <c:if test="${atts[name].image}">
         <td><s:image name="${image}"/></td>
        </c:if>
       </tr>
      </c:forEach>
      <tr><td class="form-buttons">
       <input name="file" type="file" maxlength="1000000" accept="*/*"/>
       <input value="Upload Document" name="upload" type="submit"/>
      </td></tr>
     </table>
    </div>
   </form>
  </div>
 </s:check>
 <s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <a href="../exec/login.jsp">Please login to attach files!</a>
 </s:check>
</div>