<%--
  ** Displays the snip content if it's not a weblog and the header
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<%-- Snip header, displayed only when snip is not a weblog --%>
<c:if test="${snip.name != app.configuration.startSnip}">
 <div class="snip-path"><s:path snip="${snip}"/></div>
</c:if>


<c:choose>
  <c:when test="${snip.notWeblog}">
   <div class="snip-title">
    <c:if test="${snip.notWeblog}">
      <h1 class="snip-name"><c:out value="${snip.title}"/>
        <c:if test="${snip.comment}">
         <span class="snip-commented-snip"><s:image name="commented"/> <a href="comments/<c:out value='${snip.commentedSnip.nameEncoded}'/>"><c:out value='${snip.commentedSnip.name}'/></a></span>
        </c:if>
      </h1>
    </c:if>
    <div class="snip-info">
      <c:out value="${snip.modified}" escapeXml="false"/>
      <fmt:message key="snip.viewed">
        <fmt:param value="${snip.access.viewCount}"/>
      </fmt:message>
      <fmt:message key="snip.version">
        <fmt:param value="${snip.version}"/>
      </fmt:message>
    </div>
    <div class="snip-buttons"><c:import url="util/buttons.jsp"/></div>
   </div>
  </c:when>
  <c:otherwise>
  <%-- TODO this is an ugly hack to let owners edit their buttons (see util/buttons.jsp) --%>
    <s:check permission="Edit" roles="Owner:Editor" snip="${snip}">
      <div class="snip-buttons">[<a href="exec/edit?name=<c:out value='${snip.nameEncoded}'/>"><fmt:message key="menu.edit"/></a>]</div>
    </s:check>
 </c:otherwise>
</c:choose>
<%-- Snip content --%>
<div class="snip-content">
  <c:if test="${snip.notWeblog}">
   <div class="snip-meta">
     <div class="snip-label">
       <div>
         <s:check roles="Authenticated" permission="Edit" snip="${snip}">[<a href="exec/labels?snipname=<c:out value='${snip.nameEncoded}'/>"><fmt:message key="menu.labels.add"/></a>]</s:check>
         <s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true"><span class="inactive"><fmt:message key="menu.labels"/></span></s:check>
       </div>
       <table>
         <c:forEach items="${snip.labels.all}" var="label">
           <tr><c:out value="${label.listProxy}" escapeXml="false"/></tr>
         </c:forEach>
       </table>
     </div>
     <div class="snip-attachments">
       <div>
         <s:check roles="Authenticated" permission="Edit" snip="${snip}">[<a href="exec/upload?name=<c:out value='${snip.nameEncoded}'/>"><fmt:message key="menu.attachments.add"/></a>]</s:check>
         <s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true"><span class="inactive"><fmt:message key="menu.attachments"/></span></s:check>
       </div>
       <c:out value="${snip.attachmentString}" escapeXml="false" />
     </div>
   </div>
  </c:if>
  <div>
    <%-- if there is a special view handler, use it, else display standard page --%>
    <c:choose>
     <c:when test="${not empty(view_handler)}">
      <c:catch var="error">
       <c:import url="/plugin/${view_handler}"/>
      </c:catch>
     </c:when>
     <c:otherwise>
      <c:out value="${snip.XMLContent}" escapeXml="false" />
     </c:otherwise>
    </c:choose>
  </div>
</div>