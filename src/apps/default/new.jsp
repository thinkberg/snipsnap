<%--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name"><fmt:message key="snip.create"/></h1></div>
 <form class="form" name="f" method="post" action="exec/store" enctype="multipart/form-data">
  <div class="snip-title">
     <fmt:message key="snip.name"/><br><input name="name" value="" type="text"/>
  </div>
  <c:if test="${not empty preview}">
   <div class="preview"><div class="snip-content"><c:out value="${preview}" escapeXml="false"/></div></div>
  </c:if>
  <div class="snip-content">
   <s:check roles="Authenticated">
     <div class="snip-input">
       <table>
        <tr><td><textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}" escapeXml="true"/></textarea></td></tr>
        <tr><td class="form-buttons">
         <input value="<fmt:message key="dialog.preview"/>" name="preview" type="submit"/>
         <input value="<fmt:message key="dialog.save"/>" name="save" type="submit"/>
         <input value="<fmt:message key="dialog.cancel"/>" name="cancel" type="submit"/>
        </td></tr>
       </table>
       <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
     </div>
   </s:check>
   <s:check roles="Authenticated" invert="true" >
     <fmt:message key="login.please">
       <fmt:param><fmt:message key="snip.create"/></fmt:param>
     </fmt:message>
   </s:check>
  </div>
 </form>
</div>