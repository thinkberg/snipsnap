<%--
  ** Template for copying new Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <div class="snip-title">
   <h1 class="snip-name">
     <fmt:message key="snip.copy">
       <fmt:param><c:out value="${snip.name}" escapeXml="true" /></fmt:param>
     </fmt:message>
   </h1>
 </div>
 <form class="form" name="f" method="post" action="exec/copy" enctype="multipart/form-data">
  <div class="snip-content">
   <s:check roles="Editor">
     <div class="snip-input">
       <table>
        <tr>
          <td>
            <label for="new"><fmt:message key="snip.copy.name"/></label><br/>
            <input name="name" value="<c:out value="${name}" escapeXml="true" />" type="text" size="40" tabindex="0"/>
          </td>
        </tr>
        <c:if test="${not empty subsnips}">
          <tr>
            <td>
              <label for="subsnips"><fmt:message key="snip.copy.subsnips"/></label><br/>
              <select multiple="multiple" name="subsnips" size="10" tabindex="1">
                <c:forEach items="${subsnips}" var="snip">
                  <option selected="selected" value="<c:out value='${snip.name}' escapeXml='true'/>"><c:out value="${snip.name}" escapeXml="true"/></option>
                </c:forEach>
              </select>
            </td>
          </tr>
        </c:if>
        <tr><td class="form-buttons">
         <input value="<fmt:message key="dialog.copy.snip"/>" name="copy" type="submit" tabindex="2"/>
         <input value="<fmt:message key="dialog.cancel"/>" name="cancel" type="submit" tabindex="3"/>
        </td></tr>
       </table>
       <input name="snip" type="hidden" value="<c:out value='${snip.name}' escapeXml='true'/>"/>
       <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
     </div>
   </s:check>
   <s:check roles="Authenticated" invert="true">
     <fmt:message key="login.please">
       <fmt:param><fmt:message key="dialog.copy.snip"/></fmt:param>
     </fmt:message>
   </s:check>
  </div>
 </form>
</div>