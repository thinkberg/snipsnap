<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration,
                 org.snipsnap.app.Application,
                 org.snipsnap.container.Components,
                 org.snipsnap.user.UserManager"%>
 <%--
  ** User management
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<% pageContext.setAttribute("usermanager", Components.getComponent(UserManager.class)); %>


<table class="user">
  <tr>
    <th><fmt:message key="config.users.name"/></th>
    <th><fmt:message key="config.users.lastlogin"/></th>
    <th><fmt:message key="config.users.email"/></th>
    <th><fmt:message key="config.users.roles"/></th>
    <th colspan="2"><fmt:message key="config.users.action"/></th>
  </tr>
  <c:forEach items="${usermanager.all}" var="user" varStatus="idx">
    <tr <c:choose>
      <c:when test="${idx.count mod 2 == 0}">class="user-table-odd"</c:when>
      <c:otherwise>class="user-table-even"</c:otherwise>
     </c:choose>>
      <td>
        <span class="nobr"><b><a href="space/${user.login}"><c:out value="${user.login}"/></a></b></span>
        <c:if test="${not empty user.status}"><fmt:message key="config.users.status"/> <i><c:out value="${user.status}"/></i></c:if><br/>
      </td>
      <td><c:out value="${user.lastLogin}"/></td>
      <td>
        <c:if test="${user.email != null}">
          <a href="mailto:<c:out value="${user.email}"/>"><c:out value="${user.email}"/></a>
        </c:if>
      </td>
      <td><span class="nobr"><c:out value="${user.roles}"/></span></td>
      <td>
        <input type="hidden" name="edit_login" value="<c:out value='${user.login}'/>"/>
        <input type="submit" name="edit_user" value="<fmt:message key="config.users.edit"/>"/>
      </td>
      <td>
        <c:if test="${config.adminLogin != user.login}">
          <input type="hidden" name="remove_login" value="<c:out value='${user.login}'/>"/>
          <input style="color: red" type="submit" name="ok" value="<fmt:message key="config.users.remove"/>"/>
        </c:if>
      </td>
    </tr>
  </c:forEach>
</table>
