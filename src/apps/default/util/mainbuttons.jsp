<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

[ <c:choose>
 <c:when test="${snip.name==app.configuration.startSnip}"><span class="inactive"><fmt:message key="menu.start"/></span></c:when>
 <c:otherwise><a href="space/<c:out value='${app.configuration.startSnip}'/>"><fmt:message key="menu.start"/></a></c:otherwise>
</c:choose> | <a href="space/snipsnap-index"><fmt:message key="menu.index"/></a> |
<s:check roles="Authenticated"><fmt:message key="menu.loggedIn"><fmt:param value="${app.user.login}"/></fmt:message> | <a href="exec/authenticate?logoff=true"><fmt:message key="menu.logoff"/></a></s:check>
<s:check roles="Authenticated" invert="true"><a href="exec/login.jsp"><fmt:message key="menu.login"/></a>
<c:if test="${app.configuration.allowRegister}">
  <fmt:message key="menu.or"/> <a href="exec/register.jsp"><fmt:message key="menu.register"/></a>
</c:if>
</s:check>
<s:check snip="${requestScope.snip}" roles="Owner:Editor"> |
  <c:choose>
    <c:when test="${snip.weblog}">
       <a href="exec/post.jsp?name=<c:out value='${requestScope.snip}'/>">
       <fmt:message key="menu.post"/></a>
    </c:when>
    <c:otherwise>
       <a href="exec/post.jsp?name=<c:out value='${app.configuration.startSnip}'/>">
       <fmt:message key="menu.post"/></a>
   </c:otherwise>
  </c:choose>
</s:check>
<c:if test="${app.user.admin}"> | <a href="admin/configure"><fmt:message key="menu.setup"/></a></c:if> ]
