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

<table>
  <c:choose>
    <c:when test="${not empty running && not empty running.maint}">
      <tr>
        <td><fmt:message key="config.maint.${running.maint}"/></td>
        <td>
          <c:import url="config/statusbar.jsp">
            <c:param name="statusMessage" value="config.maint.status"/>
            <c:param name="statusMax" value="${running.max}"/>
            <c:param name="statusCurrent" value="${running.current}"/>
          </c:import>
          <br/>
          <a href="configure?step=maintenance"><fmt:message key="config.maint.refresh"/></a>
        </td>
      </tr>
    </c:when>
    <c:otherwise>
      <c:if test="${not empty fixComments}">
        <tr>
          <td><fmt:message key="config.maint.fix.comments"/></td>
          <td>
            <select name="fixComments" size="5" disabled="disabled">
              <c:forEach items="${fixComments}" var="comment">
                <option selected="selected" value="<c:out value='${comment.name}' escapeXml='true'/>"><c:out value="${comment.name}" escapeXml="true"/></option>
              </c:forEach>
            </select>
          </td>
        </tr>
      </c:if>
      <c:if test="${not empty fixParents}">
        <tr>
          <td><fmt:message key="config.maint.fix.parents"/></td>
          <td>
            <select name="fixParents" size="5" disabled="disabled">
              <c:forEach items="${fixParents}" var="post">
                <option selected="selected" value="<c:out value='${post.name}' escapeXml='true'/>"><c:out value="${post.name}" escapeXml="true"/></option>
              </c:forEach>
            </select>
          </td>
        </tr>
      </c:if>
      <c:if test="${not empty duplicates}">
        <tr>
          <td><fmt:message key="config.maint.fix.duplicates"/></td>
          <td>
            <select name="duplicates" size="5" disabled="disabled">
              <c:forEach items="${duplicates}" var="snip">
                <option selected="selected" value="<c:out value='${snip.name}' escapeXml='true'/>"><c:out value="${snip.name}" escapeXml="true"/></option>
              </c:forEach>
            </select>
          </td>
        </tr>
      </c:if>
      <c:if test="${not empty notFixable}">
        <tr>
          <td><fmt:message key="config.maint.not.fixable"/></td>
          <td>
            <select name="notFixable" size="5" disabled="disabled">
              <c:forEach items="${notFixable}" var="snip">
                <option selected="selected" value="<c:out value='${snip.name}' escapeXml='true'/>"><c:out value="${snip.name}" escapeXml="true"/></option>
              </c:forEach>
            </select>
          </td>
        </tr>
      </c:if>
      <tr>
        <td><fmt:message key="config.maint.info"/></td>
        <td>
          <input type="hidden" name="step" value="maintenance"/>
          <input type="submit" name="check" value="<fmt:message key="config.maint.check"/>"
            <c:if test="${not empty status}">disabled="disabled"</c:if>
          />
          <input type="submit" name="dorepair" value="<fmt:message key="config.maint.fix"/>"
            <c:if test="${empty fixComments && empty fixParents}">disabled="disabled"</c:if>
          />
        </td>
      </tr>
    </c:otherwise>
  </c:choose>
</table>