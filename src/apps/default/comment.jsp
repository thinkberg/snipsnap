<%--
  ** Snip comments display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<div class="snip-wrapper">
 <%-- include snip header and content --%>
 <c:import url="util/snip-base.jsp"/>
 <%-- comments --%>
 <div class="snip-comments">
  <c:forEach items="${snip.comments.comments}" var="comment" >
   <%-- title/header of a comment --%>
   <div class="comment-title">
    <h2 class="comment-name"><s:image name="comment"/> <c:out value="${comment.modified.short}" escapeXml="false" /></h2>
    <s:check roles="Owner" permission="Edit" snip="${comment}">
      <div class="comment-buttons">[<a href="../exec/edit?name=<c:out value='${comment.name}'/>">edit</a>]</div>
    </s:check>
   </div>
   <%-- content --%>
   <div class="comment-content"><c:out value="${comment.XMLContent}" escapeXml="false" /></div>
  </c:forEach>
  <%-- input field --%>
  <s:check roles="Authenticated">
   <div class="comment-input">
    <a name="post"></a>
    <div class="preview"><div class="comment-content"><c:out value="${preview}" escapeXml="false"/></div></div>
    <form class="form" name="f" method="post" action="../exec/storecomment#post">
     <table>
      <tr><td><textarea name="content" type="text" cols="80" rows="20" tabindex="0"><c:out value="${content}" escapeXml="false"/></textarea></td></tr>
      <tr><td class="form-buttons">
       <input value="Preview" name="preview" type="submit"/>
       <input value="Comment" name="save" type="submit"/>
       <input value="Cancel" name="cancel" type="submit"/>
      </td></tr>
     </table>
     <input name="comment" type="hidden" value="<c:out value="${snip.name}"/>"/>
     <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>"/>
    </form>
   </div>
  </s:check>
  <s:check roles="Authenticated" invert="true" >
   Please <a href="../exec/login.jsp">login</a> to post a comment.
  </s:check>
 </div>
</div>


