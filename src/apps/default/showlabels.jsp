<%@ page import="org.radeox.util.Encoder"%>
 <%--
  ** Template for managing Labels
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
      <fmt:message key="snip.labels.title">
       <fmt:param value="${snip.name}"/>
      </fmt:message>
     </h1>
    </div>
    <div class="snip-content">
      <c:if test="${error != null}">
        <div class="error"><c:out value="${error}"/></div>
        <p/>
      </c:if>
      <div class="snip-input">
         <script type="text/javascript" language="Javascript">
          <!--
          function checkAll(checkbox) {
            if(checkbox.name == 'allChecked') {
              <c:forEach items="${snip.labels.all}" var="label" varStatus="status" >
              document.getElementById("<c:out value="label${status.index}"/>").checked = checkbox.checked
              </c:forEach>
            }
          }
          -->
        </script>
        <form class="form" name="f" method="post" action="exec/labels" enctype="multipart/form-data">
          <input name="snipname" type="hidden" value="<c:out value="${snip.name}"/>"/>
          <input name="referer" type="hidden" value="<%= Encoder.escape(request.getHeader("REFERER")) %>"/>
          <table class="wiki-table" border="0" cellpaddin="0" cellspacing="0">
            <tr>
             <s:check roles="Authenticated"><th><input id="all" type="checkbox" name="allChecked" onClick="return checkAll(this);"/></th></s:check>
             <th><fmt:message key="snip.labels.name"/></th>
             <th><fmt:message key="snip.labels.type"/></th>
             <th><fmt:message key="snip.labels.value"/></th>
             <th></th>
            </tr>
            <c:forEach items="${snip.labels.all}" var="label" varStatus="status">
             <tr>
               <s:check roles="Authenticated"><td><input id="label<c:out value='${status.index}'/>" type="checkbox" name="label" value="<c:out value='${label.name}' escapeXml="true"/>"/></td></s:check>
               <td><c:out value="${label.name}" escapeXml="true" /></td>
               <td><c:out value="${label.type}" escapeXml="true" /></td>
               <td><c:out value="${label.value}" escapeXml="true" /></td>
               <td>[<a href="exec/labels?snipname=<c:out value="${snip.nameEncoded}"/>&amp;labelname=<c:out value="${label.name}"/>&amp;edit=edit"><fmt:message key="snip.labels.edit"/></a>]</td>
             </tr>
            </c:forEach>
            <c:if test="${empty(snip.labels.all)}">
              <tr><td colspan="5"><fmt:message key="snip.labels.nolabels"/></td></tr>
            </c:if>
            <tr>
              <td colspan="5" class="form-buttons">
                <s:check roles="Authenticated" permission="Edit" snip="${snip}">
                  <select name="labeltype">
                    <c:forEach items="${labelmanager.types}" var="type" varStatus="status">
                      <option><c:out value="${type}"/></option>
                    </c:forEach>
                  </select>
                  <input value="<fmt:message key='snip.labels.add'/>" name="add" type="submit"/>
                  <br/>
                  <input value="<fmt:message key='snip.labels.delete'/>" name="delete" type="submit"/>
                </s:check>
                <input value="<fmt:message key="dialog.back.to"><fmt:param value="${snip.name}"/></fmt:message>" name="cancel" type="submit"/>
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
