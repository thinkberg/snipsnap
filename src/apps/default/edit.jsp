<%@ page import="org.radeox.util.Encoder"%>
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
   <h1 class="snip-name"><c:out value="${snip_name}" escapeXml="true"/></h1>
  </div>
  <c:if test="${not empty preview}">
   <div class="preview"><div class="snip-content"><c:out value="${preview}" escapeXml="false"/></div></div>
  </c:if>
  <div class="snip-content">
   <s:check roles="Authenticated" permission="Edit" >
     <div class="snip-input">
      <form class="form" name="f" method="post" action="exec/store" enctype="multipart/form-data">
       <table>
        <tr><td><textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}" escapeXml="true"/></textarea></td></tr>
        <tr><td class="form-buttons">
         <input value="<fmt:message key="snip.edit.help"/>" onClick="showHide('help'); return false;" type="submit">
         <input value="<fmt:message key='dialog.preview'/>" name="preview" type="submit"/>
         <input value="<fmt:message key='dialog.save'/>" name="save" type="submit"/>
         <input value="<fmt:message key='dialog.cancel'/>" name="cancel" type="submit"/>
        </td></tr>
       </table>
       <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
       <input name="referer" type="hidden" value="<%= Encoder.escape(request.getHeader("REFERER")) %>"/>
      </form>
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
   <s:check roles="Authenticated" permission="Edit" invert="true" >
     <fmt:message key="login.please">
       <fmt:param><fmt:message key="edit.snip"/></fmt:param>
     </fmt:message>
   </s:check>
  </div>
</div>