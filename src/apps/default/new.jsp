<%@ page import="org.radeox.util.Encoder"%>
 <%--
  ** Template for creating new Snips.
  ** @author Stephan J. Schmidt
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name"><fmt:message key="snip.create"/></h1></div>
  <form class="form" name="f" method="post" action="exec/store" enctype="multipart/form-data">
   <s:check permission="CREATE_SNIP">
    <c:if test="${not empty preview}">
     <div class="preview"><div class="snip-content"><c:out value="${preview}" escapeXml="false"/></div></div>
    </c:if>

    <div class="snip-title">
      <fmt:message key="snip.parent"/><br/>
      <input name="parentBefore" value="<c:out value="${parentBefore}"/>" type="hidden"/>
      <s:pathSelector parentName="${parentBefore}"/><br/>
      <fmt:message key="snip.name"/><br/><input name="name" value="<c:out value="${name}"/>" type="text"/>
      <c:if test="${error == 'snip.name.empty'}"><span class="error"><fmt:message key="snip.name.empty"/></span></c:if>
    </div>

    <div class="snip-content">
     <div class="snip-input">
       <table>
        <tr><td><textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}" escapeXml="true"/></textarea></td></tr>
        <tr><td class="form-buttons">
          <c:if test="${not empty templates}">
            <fmt:message key="snip.template"/>
            <select name="template" size="1">
              <c:forEach items="${templates}" var="template" >
                <option><c:out value="${template}"/>
              </c:forEach>
            </select>
            <input value="<fmt:message key="dialog.copy.template"/>" name="copy.template" type="submit"/>
            <br/>
          </c:if>
          <input value="<fmt:message key="dialog.preview"/>" name="preview" type="submit"/>
          <input value="<fmt:message key="dialog.save"/>" name="save" type="submit"/>
          <input value="<fmt:message key="dialog.cancel"/>" name="cancel" type="submit"/>
        </td></tr>
       </table>
       <input name="mime_type" type="hidden" value="<c:out value="${mime_type}"/>"/>
       <input name="edit_handler" type="hidden" value="<c:out value="${edit_handler}"/>"/>
       <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
       <input name="referer" type="hidden" value="<%= Encoder.escape(request.getHeader("REFERER")) %>"/>
      </div>
    </div>
    <script language="Javascript" type="text/javascript">
     <!--
      function showHide(obj) {
        if (document.layers) {
          current = (document.layers[obj].display == 'none') ? 'block' : 'none';
          document.layers[obj].display = current;
        } else if (document.all) {
          current = (document.all[obj].style.display == 'none') ? 'block' : 'none';
          document.all[obj].style.display = current;
        } else if (document.getElementById) {
          vista = (document.getElementById(obj).style.display == 'none') ? 'block' : 'none';
          document.getElementById(obj).style.display = vista;
        }
      }
     // -->
     </script>
     <div id="help" class="snip-help" style="display: none">
       <s:snip name="snipsnap-help"/>
     </div>
   </s:check>
   <s:check permission="CREATE_SNIP" invert="true" >
     <fmt:message key="login.please">
       <fmt:param><fmt:message key="snip.create"/></fmt:param>
     </fmt:message>
   </s:check>
 </form>
</div>
