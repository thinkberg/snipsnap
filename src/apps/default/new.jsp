<%--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
  <div class="snip-title"><h1 class="snip-name">Create new Snip:</h1></div>
 <form class="form" name="f" method="post" action="<c:out value='${app.configuration.path}'/>/exec/store" enctype="multipart/form-data">
  <div class="snip-title">
     Name<br><input name="name" value="" type="text"/>
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
         <input value="Preview" name="preview" type="submit"/>
         <input value="Save" name="save" type="submit"/>
         <input value="Cancel" name="cancel" type="submit"/>
        </td></tr>
       </table>
       <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
     </div>
   </s:check>
   <s:check roles="Authenticated" invert="true" >
    Please <a href="<c:out value='${app.configuration.path}'/>/exec/login.jsp">login</a> to create this snip.
   </s:check>
  </div>
  </form>
</div>