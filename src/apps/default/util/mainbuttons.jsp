<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

[ <c:choose>
 <c:when test="${snip.name=='start'}"><span class="inactive"><fmt:message key="menu.start"/></span></c:when>
 <c:otherwise><a href="<c:out value='${app.configuration.path}'/>/space/<c:out value='${app.configuration.startSnip}'/>"><fmt:message key="menu.start"/></a></c:otherwise>
</c:choose> | <a href="<c:out value='${app.configuration.path}'/>/space/snipsnap-index"><fmt:message key="menu.index"/></a> |
<s:check roles="Authenticated"><fmt:message key="menu.loggedIn"/><a href="<c:out value='${app.configuration.path}/space/${app.user.login}'/>"><c:out value="${app.user.login}"/></a> | <a href="<c:out value='${app.configuration.path}'/>/exec/authenticate?logoff=true"><fmt:message key="menu.logoff"/></a></s:check>
<s:check roles="Authenticated" invert="true"><a href="<c:out value='${app.configuration.path}'/>/exec/login.jsp"><fmt:message key="menu.login"/></a>
<c:if test="${app.configuration.allowRegister}">
  <fmt:message key="menu.or"/> <a href="<c:out value='${app.configuration.path}'/>/exec/register.jsp"><fmt:message key="menu.register"/></a>
</c:if>
</s:check>
<s:check roles="Editor"> | <a href="<c:out value='${app.configuration.path}'/>/exec/post.jsp"><fmt:message key="menu.post"/></a></s:check>
<c:if test="${app.user.admin}"> | <a href="<c:out value='${app.configuration.path}'/>/manager/"><fmt:message key="menu.manager"/></a></c:if> ]
