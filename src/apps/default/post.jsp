<%--
  ** weblog post template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <div class="snip-title"><h1 class="snip-name">Post To Weblog</h1></div>
 <div class="snip-content">
  <s:check roles="Editor">
  <c:if test="${not empty preview}">
   Preview:
   <div class="preview"><div class="snip-content"><c:out value="${preview}" escapeXml="false"/></div></div>
  </c:if>
   <form class="form" method="post" action="../exec/storepost">
    <table>
     <tr><td>Title:<br><input name="title"/></td></tr>
     <tr><td><textarea name="content" type="text" cols="80" rows="20"></textarea></td></tr>
     <tr><td class="form-buttons">
      <input value="Preview" name="preview" type="submit"/>
      <input value="Post" name="save" type="submit"/>
      <input value="Cancel" name="cancel" type="submit"/>
     </td></tr>
    </table>
    <input name="post" type="hidden" value="weblog"/>
    <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
   </form>
  </s:check>
  <s:check roles="Editor" invert="true">
   Please <a href="../exec/login.jsp">login!</a> as editor.
  </s:check>
 </div>
</div>
