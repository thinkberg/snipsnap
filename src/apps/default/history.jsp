<%--
  ** Snip history display template.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <%-- include snip header and content --%>
 <div class="snip-title">
   <h1 class="snip-name"><fmt:message key="snip.history.title"/> <c:out value="${snip}" escapeXml="false"/></h1>
 </div>

 <table class="wiki-table">
   <tr>
     <th><fmt:message key="snip.history.version"/></th>
     <th><fmt:message key="snip.history.user"/></th>
     <th><fmt:message key="snip.history.date"/></th><th><fmt:message key="snip.history.size"/></th><th><fmt:message key="snip.history.views"/></th>
     <th></th>
   </tr>
   <c:forEach items="${history}" var="info" >
     <tr><td><a href="exec/version?name=<c:out value='${snip.nameEncoded}'/>&amp;version=<c:out value="${info.version}"/>">#<c:out value="${info.version}"/></a>
       </td><td><c:out value="${info.MUser}"/></td>
       <td><c:out value="${info.MTime}"/></td><td><c:out value="${info.size}"/></td><td><c:out value="${info.viewCount}"/></td>
       <td>
       <c:if test="${info.version > 1}">
         <a href="exec/diff?name=<c:out value='${snip.nameEncoded}'/>&amp;oldVersion=<c:out value="${info.version-1}"/>&amp;newVersion=<c:out value="${info.version}"/>">Changes from <c:out value="${info.version-1}"/> to <c:out value="${info.version}"/></a>
       </c:if>
       </td>
     </tr>
   </c:forEach>
   <tr>
     <td class="form-buttons" colspan="4">
       <input value="<fmt:message key="dialog.back.to"><fmt:param value="${snip.nameEncoded}"/></fmt:message>" name="cancel" type="submit"/>
     </td>
   </tr>
 </table>

</div>
