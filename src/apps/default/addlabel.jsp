<%@ page import="org.radeox.util.Encoder"%>
 <%--
  ** Template for adding Labels
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<div class="snip-wrapper">
  <s:check roles="Authenticated" permission="Edit" snip="${snip}">
    <div class="snip-title">
     <h1 class="snip-name">
      <c:choose>
       <c:when test="${empty(edit)}">
        <fmt:message key="snip.labels.add.title">
         <fmt:param value="${snip.name}"/>
         <fmt:param value="${label.type}"/>
        </fmt:message>
       </c:when>
       <c:otherwise>
        <fmt:message key="snip.labels.edit.title">
         <fmt:param value="${snip.name}"/>
         <fmt:param value="${label.type}"/>
        </fmt:message>
       </c:otherwise>
      </c:choose>
     </h1>
    </div>
    <div class="snip-content">
      <c:if test="${error != null}">
        <div class="error"><c:out value="${error}"/></div>
        <p/>
      </c:if>
      <div class="snip-input">
        <form class="form" name="f" method="post" action="exec/storelabel" enctype="multipart/form-data">
          <input name="snipname" type="hidden" value="<c:out value="${snip.name}"/>"/>
          <input name="labeltype" type="hidden" value="<c:out value="${label.type}"/>"/>
          <input name="labelname" type="hidden" value="<c:out value="${label.name}"/>"/>
          <table border="0" cellpadding="0" cellspacing="0">
            <tr><td><c:out value="${label.inputProxy}" escapeXml="false" /></td></tr>
            <tr>
              <td colspan="3" class="form-buttons">
                <c:choose>
                  <c:when test="${empty(edit)}">
                    <input value="<fmt:message key='snip.labels.add'/>" name="add" type="submit"/>
                  </c:when>
                  <c:otherwise>
                    <input value="<fmt:message key='snip.labels.save'/>" name="add" type="submit"/>
                  </c:otherwise>
                </c:choose>
                <input value="<fmt:message key='dialog.cancel'/>" name="back" type="submit"/>
                <input value="<fmt:message key='dialog.back.to'><fmt:param value='${snip.name}'/></fmt:message>" name="cancel" type="submit"/>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </div>
  </s:check>
  <s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
    <fmt:message key="login.please">
     <fmt:param><fmt:message key="snip.labels.login"/></fmt:param>
    </fmt:message>
  </s:check>
</div>