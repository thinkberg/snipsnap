<%@ page import="org.snipsnap.user.UserManager,
                 org.snipsnap.container.Components,
                 java.util.List"
 %>
<%--
  ** User management: list existing users
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="users-pages">
  <fmt:message key="config.users.page"/>
  <%
    UserManager um = (UserManager) Components.getComponent(UserManager.class);
    String startStr = request.getParameter("start");
    if (startStr != null && !"".equals(startStr)) {
      session.setAttribute("__usersStart", startStr);
    } else {
      session.setAttribute("__usersStart", "0");
    }
    int start = Integer.parseInt((String) session.getAttribute("__usersStart"));

    String displayCountStr = request.getParameter("show");
    if (displayCountStr != null && !"".equals(displayCountStr)) {
      session.setAttribute("__displayCount", displayCountStr);
    } else {
      session.setAttribute("__displayCount", "15");
    }
    int displayCount = Integer.parseInt((String) session.getAttribute("__displayCount"));
    int pageCounter = 1;
    List usersList = um.getAll();
    for (int count = 0; count < usersList.size(); count += displayCount) {
      if (count < start || count >= start + displayCount) {
        out.print(" <a href=\"configure?select=users&amp;start=" + count + "\">" + (pageCounter++) + "</a>");
      } else {
        out.print(" " + pageCounter++);
      }
    }

    pageContext.setAttribute("users",
                             usersList.subList(start, Math.min(usersList.size(), start + displayCount)));
  %>
</div>
<table>
  <tr>
    <th><fmt:message key="config.users.name"/></th>
    <th><fmt:message key="config.users.email"/></th>
    <th><fmt:message key="config.users.roles"/></th>
    <th><fmt:message key="config.users.lastlogin"/></th>
    <th colspan="2"><fmt:message key="config.users.action"/></th>
  </tr>
  <c:forEach items="${users}" var="user" varStatus="idx">
    <tr <c:choose>
      <c:when test="${idx.count mod 2 == 0}">class="users-odd"</c:when>
      <c:otherwise>class="users-even"</c:otherwise>
     </c:choose>>
      <td>
        <span class="nobr"><b><a href="space/<c:out value='${user.login}'/>"><c:out value="${user.login}"/></a></b></span>
        <c:if test="${user.status != 'not set'}"><br/><fmt:message key="config.users.status"/> <i><c:out value="${user.status}"/></i></c:if><br/>
      </td>
      <td>
        <c:if test="${user.email != null}">
          <a href="mailto:<c:out value="${user.email}"/>"><c:out value="${user.email}"/></a>
        </c:if>
      </td>
      <td><span class="nobr"><c:out value="${user.roles}"/></span></td>
      <td><fmt:formatDate pattern="yy-MM-dd hh:mm" value="${user.lastLogin}"/></td>
      <td>
        <input type="hidden" name="edit_login" value="<c:out value='${user.login}'/>"/>
        <input type="submit" name="edit_user" value="<fmt:message key="config.users.edit"/>"/>
      </td>
      <td>
        <c:if test="${configuser.login != user.login}">
          <input type="hidden" name="remove_login" value="<c:out value='${user.login}'/>"/>
          <input style="color: red" type="submit" name="ok" value="<fmt:message key="config.users.remove"/>"/>
        </c:if>
      </td>
    </tr>
  </c:forEach>
</table>