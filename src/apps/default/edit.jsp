<%--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
  <div class="snip-title">
   <h1 class="snip-name"><c:out value="${snip_name}" escapeXml="false"/></h1>
  </div>
  <c:if test="${not empty preview}">
   <div class="preview"><div class="snip-content"><c:out value="${preview}" escapeXml="false"/></div></div>
  </c:if>
  <div class="snip-content">
   <s:check roles="Authenticated">
     <div class="snip-input">
      <form class="form" name="f" method="post" action="exec/store" enctype="multipart/form-data">
       <table>
        <tr><td><textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}" escapeXml="true"/></textarea></td></tr>
        <tr><td class="form-buttons">
         <input value="Preview" name="preview" type="submit"/>
         <input value="Save" name="save" type="submit"/>
         <input value="Cancel" name="cancel" type="submit"/>
        </td></tr>
       </table>
       <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
       <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
      </form>
     </div>
   </s:check>
   <s:check roles="Authenticated" invert="true" >
     <fmt:message key="login.please">
       <fmt:param><fmt:message key="edit.snip"/></fmt:param>
     </fmt:message>
   </s:check>
  </div>
</div>