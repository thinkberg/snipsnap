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

 <div class="snip-content">
   <table class="wiki-table" cellpadding="0" cellspacing="0" border="0">
     <tr>
       <th><fmt:message key="snip.history.version"/></th>
       <th><fmt:message key="snip.history.user"/></th>
       <th><fmt:message key="snip.history.date"/></th>
       <th><fmt:message key="snip.history.size"/></th>
       <th><fmt:message key="snip.history.views"/></th>
       <th></th>
     </tr>
     <c:forEach items="${history}" var="info" >
       <tr><td><a href="exec/version?name=<c:out value='${snip.nameEncoded}'/>&amp;version=<c:out value="${info.version}"/>">#<c:out value="${info.version}"/></a>
         </td><td><c:out value="${info.MUser}"/></td>
         <td><fmt:formatDate type="both" value="${info.MTime}" dateStyle="LONG" timeStyle="SHORT" /></td>
         <td><c:out value="${info.size}"/></td>
         <td><c:out value="${info.viewCount}"/></td>
         <td>
         <c:if test="${info.version > 1}">
           <a href="exec/diff?name=<c:out value='${snip.nameEncoded}'/>&amp;oldVersion=<c:out value="${info.version-1}"/>&amp;newVersion=<c:out value="${info.version}"/>"><fmt:message key="snip.diff.changes">
             <fmt:param value="${info.version-1}"/>
             <fmt:param value="${info.version}"/>
           </fmt:message></a>
         </c:if>
         </td>
       </tr>
     </c:forEach>
     <tr>
       <td class="form-buttons" colspan="5">
         <form class="form" name="f" method="get" action="space/<c:out value='${snip.nameEncoded}'/>">
           <input value="<fmt:message key="dialog.back.to"><fmt:param value="${snip.name}"/></fmt:message>" type="submit"/>
       </form>
       </td>
     </tr>
   </table>
 </div>

</div>
