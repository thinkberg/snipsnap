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
  <%-- display snip name or just "create new snip" if there is no existing snip --%>
  <div class="snip-title">
   <h1 class="snip-name">
    <c:choose>
     <c:when test="${not empty(snip) && not empty(snip_name)}">
      <c:out value="${snip_name}" escapeXml="true"/>
     </c:when>
     <c:otherwise>
      <fmt:message key='snip.create'/>
     </c:otherwise>
    </c:choose>
   </h1>
  </div>
  <%-- preview existing snip content --%>
  <c:if test="${not empty(preview)}">
   <div class="preview"><div class="snip-content"><c:out value="${preview}" escapeXml="false"/></div></div>
  </c:if>

  <%-- display edit part --%>
  <div class="snip-content">
   <s:check roles="Authenticated" permission="Edit" >
     <div class="snip-input">
      <form class="form" name="f" method="post" action="exec/store" enctype="multipart/form-data">
        <%-- display parent selector and an editable snip name in case --%>
        <c:if test="${empty(snip)}">
          <table>
            <tr>
              <td><fmt:message key="snip.parent"/></td>
              <td>
                <input name="parentBefore" value="<c:out value="${parentBefore}"/>" type="hidden"/>
                <s:pathSelector parentName="${parentBefore}" selected="${parent}"/>
              </td>
            </tr>
            <tr>
              <td><fmt:message key="snip.name"/></td>
              <td>
                <input name="name" value="<c:out value="${snip_name}"/>" type="text"/>
                <c:if test="${error == 'snip.name.empty'}"><span class="error"><fmt:message key="snip.name.empty"/></span></c:if>
              </td>
            </tr>
          </div>
        </c:if>
        <table>
         <tr><td>
          <%-- if there is a special edit handler, use it, else display standard page --%>
          <c:choose>
           <c:when test="${not empty(edit_handler)}">
            <c:catch var="error">
             <c:import url="/plugin/${edit_handler}"/>
            </c:catch>
           </c:when>
           <c:otherwise>
            <textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}" escapeXml="true"/></textarea>
           </c:otherwise>
          </c:choose>
         </td></tr>
         <%-- display template copy option (only if default edit handler)--%>
         <tr><td class="form-buttons">
          <c:if test="${empty(error)}">
             <c:if test="${not empty(templates) && empty(edit_handler)}">
              <fmt:message key="snip.template"/>
              <select name="template" size="1">
                <c:forEach items="${templates}" var="template" >
                  <option><c:out value="${template}"/>
                </c:forEach>
              </select>
              <input value="<fmt:message key="dialog.copy.template"/>" name="copy.template" type="submit"/>
            </c:if>
            <%-- default buttons when editing a snip --%>
            <input value="<fmt:message key="snip.edit.help"/>" onClick="showHide('help'); return false;" type="submit">
            <input value="<fmt:message key='dialog.preview'/>" name="preview" type="submit"/>
            <input value="<fmt:message key='dialog.save'/>" name="save" type="submit"/>
          </c:if>
          <input value="<fmt:message key='dialog.cancel'/>" name="cancel" type="submit"/>
          <c:choose>
           <c:when test="${error == 'snip.store.handler.error'}">
            <br/>
            <span class="error">
              <fmt:message key="${error}">
                <fmt:param value="${error_msg}"/>
              </fmt:message>
            </span>
           </c:when>
           <c:when test="${not empty(error)}">
            <br/><span class="error"><c:out value="${error}"/></span>
           </c:when>
          </c:choose>
         </td></tr>
        </table>

        <%-- keep variables in sync --%>
        <input name="mime_type" type="hidden" value="<c:out value="${mime_type}"/>"/>
        <input name="edit_handler" type="hidden" value="<c:out value="${edit_handler}"/>"/>
        <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
        <input name="referer" type="hidden" value="<c:out value="${referer}"/>" />
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