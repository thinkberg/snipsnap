<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<font size="2">[ <a href="<c:url value='/space/start'/>">start</a> | <a href="<c:url value='/space/snipsnap-index'/>">index</a> |
 <s:check roles="Authenticated">
    logged in as <a href="<c:url value='/space/${app.user.login}'/>"><c:out value="${app.user.login}"/></a> | <a href="<c:url value='/exec/authenticate?logoff=true'/>">logoff</a>
 </s:check>
 <s:check roles="Authenticated" invert="true">
  <a href="<c:url value='/exec/login.jsp'/>">login</a> or <a href="<c:url value='/exec/register.jsp'/>">register</a>
 </s:check>
 <s:check roles="Editor">
     | <a href="<c:url value='/exec/post.jsp'/>">post blog</a>
 </s:check>
 <c:if test="${app.user.admin}">
     | <a href="<c:url value='/exec/admin/'/>">admin</a>
 </c:if> ]
</font>
