<%@ page import="org.snipsnap.snip.SnipSpace,
                 java.util.Collection,
                 java.util.Set,
                 java.util.HashSet,
                 java.util.List,
                 java.util.Collections,
                 java.util.ArrayList,
                 org.snipsnap.snip.Snip,
                 java.util.Iterator"%>
 <%--
  ** Maintenance
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
  SnipSpace space = (SnipSpace) pageContext.findAttribute("space");
  Snip[] list = space.match("comment-");
  List toRepair = new ArrayList();
  List notRepairable = new ArrayList();
  for (int s = 0; s < list.length; s++) {
    if (null == list[s].getCommentedSnip() || null == list[s].getCommentedName() || "".equals(list[s].getCommentedName())) {
      String snipName = list[s].getName();
      String commentedName = snipName.substring("comment-".length(), snipName.lastIndexOf("-"));
      if (!space.exists(commentedName)) {
        notRepairable.add(list[s]);
      } else {
        toRepair.add(list[s]);
      }
    }
  }
  pageContext.setAttribute("toRepair", new Integer(toRepair.size()));
  pageContext.setAttribute("noRepair", notRepairable);
  if (request.getParameter("dorepair") != null) {
    Iterator it = toRepair.iterator();
    while (it.hasNext()) {
      Snip snip = (Snip) it.next();
      String snipName = snip.getName();
      String commentedName = snipName.substring("comment-".length(), snipName.lastIndexOf("-"));
      snip.setCommentedName(commentedName);
      space.systemStore(snip);
      System.err.println("fixing: '" + snip + "' -> '" + commentedName + "'");
      it.remove();
    }
  }
%>

<table>
  <tr>
    <td><fmt:message key="config.maint.text"/></td>
    <td>
      <fmt:message key="config.maint.need.fix"><fmt:param><c:out value="${toRepair}"/></fmt:param></fmt:message><br/>
    </td>
  </tr>
  <c:if test="${not empty noRepair}">
  <tr>
    <td><fmt:message key="config.maint.not.fixable"/></td>
    <td>
      <c:forEach items="${noRepair}" var="snip">
        <c:out value="${snip.name}" escapeXml="true"/><br/>
      </c:forEach>
    </td>
  </tr>
  </c:if>
  <tr>
    <td> </td>
    <td>
      <input type="hidden" name="step" value="maintenance"/>
      <input type="submit" name="dorepair" value="<fmt:message key="config.maint.fix"/>"
        <c:if test="${toRepair == 0}">disabled="disabled"</c:if>
      />
    </td>
  </tr>
</table>