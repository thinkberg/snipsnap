<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<font size="2">[ <a href="../space/start">start</a> | <a href="../space/snipsnap-index">index</a> |
 <s:check roles="Authenticated">
    logged in as <a href="../space/<c:out value='${app.user.login}'/>"><c:out value="${app.user.login}"/></a> | <a href="../exec/authenticate?logoff=true">logoff</a>
 </s:check>
 <s:check roles="Authenticated" invert="true">
  <a href="../exec/login.jsp">login</a> or <a href="../exec/register.jsp">register</a>
 </s:check>
 <s:check roles="Editor">
     | <a href="../exec/post.jsp">post blog</a>
 </s:check>
 <c:if test="${app.user.admin}">
     | <a href="../exec/admin">admin</a>
 </c:if> ]
</font>
