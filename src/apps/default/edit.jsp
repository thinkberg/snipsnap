<%--
  ** Template for editing Snips.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <s:check roles="Authenticated" permission="Edit" snip="${snip}">
  <div class="snip-title">
   <h1 class="snip-name"><c:out value="${snip_name}" escapeXml="false"/></h1>
  </div>
  <c:if test="${not empty preview}">
   <div class="preview"><div class="snip-content"><c:out value="${preview}" escapeXml="false"/></div></div>
  </c:if>
  <div class="snip-content">
   <div class="snip-input">
    <form class="form" name="f" method="post" action="../exec/store" enctype="multipart/form-data">
     <table>
      <tr><td><textarea name="content" type="text" cols="80" rows="20"><c:out value="${content}" escapeXml="false"/></textarea></td></tr>
      <tr><td class="form-buttons">
       <input value="Preview" name="preview" type="submit"/>
       <input value="Save" name="save" type="submit"/>
       <input value="Cancel" name="cancel" type="submit"/>
      </td></tr>
     </table>
     <input name="name" type="hidden" value="<c:out value="${snip_name}"/>"/>
     <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
     <%-- @TODO refactor for use with other forms --%>
     <a href="#" onClick="showHide('imageUpload'); return false;">Show/Hide Image Upload</a><p/>
     <div id="imageUpload" style="display: none;" class="preview">
      <table border="0" cellpaddin="0" cellspacing="0">
       <c:forEach items="${images}" var="image">
        <tr><td>{image:<c:out value="${ids[image]}"/>}</td><td><s:image name="${image}"/></td></tr>
       </c:forEach>
      </table>
      <p/>
      <c:if test="${error != null}">
       <div class="error"><c:out value="${error}"/></div>
       <p/>
      </c:if>
      Upload Image: <input name="image" type="file" maxlength="1000000" accept="image/*"/>
      <input value="Upload" name="upload" type="submit"/>
     </div>
    </form>
   </div>
  </div>
 </s:check>
 <s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <a href="../exec/login.jsp">Please login!</a>
 </s:check>
</div>