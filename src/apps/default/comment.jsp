<!--
  ** Snip comments display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<table width="100%" border="0" cellspacing="2" cellpadding="1">
 <c:if test="${snip.notWeblog}">
  <tr>
   <td><span class="snip-name"><c:out value="${snip.name}"/></span></td>
   <td>
    <s:check roles="Authenticated" permission="Edit" snip="${snip}">
     [<a href="../exec/edit?name=<c:out value='${snip.name}'/>">edit</a>]
    </s:check>
    <s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
     <span class="inactive">[edit]</span>
    </s:check>
   </td>
  </tr>
  <tr width="100%">
   <td colspan="2">
    <span class="snip-modified"><c:out value="${snip.modified}" 
          escapeXml="false"/></span>
   </td>
  </tr>
 </c:if>
 <tr>
  <td colspan="2" width="100%">
   <c:out value="${snip.XMLContent}" escapeXml="false" />
  </td>
 </tr>
 <tr><td colspan="2" height="20"></td></tr>
 <tr><td colspan="2">
  <table width="100%" border="0" cellspacing="2" cellpadding="1">
   <c:forEach items="${snip.comments.comments}" var="comment" >
    <tr>
     <td><img src="../images/comment.png"/></td>
     <td>
      <span class="comment-author"><c:out value="${comment.modified.short}" 
                                      escapeXml="false" /></span>
       <s:check roles="Owner" permission="Edit" snip="${comment}">
        [<a href="../exec/edit?name=<c:out value='${comment.name}'/>">edit</a>]
       </s:check>
      </td>
     </tr>
     <tr>
      <td></td>
      <td width="100%">
       <c:out value="${comment.XMLContent}" escapeXml="false" />
      </td>
     </tr>
     <tr><td height="20"></td></tr>
    </c:forEach>
   </table>
  </td>
 </tr>
 <tr>
  <td colspan="2">
   <a name="post"></a>
   <s:check roles="Authenticated">
    <form name="f" method="POST" action="../exec/storecomment">
     <table border="0" cellpadding="0" cellspacing="2">
      <tr><td><textarea name="content" type="text" cols="80" rows="20" tabindex="0"></textarea></td></tr>
      <tr><td align="right">
       <input value="Comment" name="save" type="submit">
       <input value="Cancel" name="cancel" type="submit">
      </td></tr>
     </table>
     <input name="comment" type="hidden" value="<c:out value="${snip.name}"/>">
     <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
    </form>
   </s:check>
   <s:check roles="Authenticated" invert="true" >
    Please <a href="../exec/login.jsp">login</a> to post a comment.
   </s:check>
  </td>
 </tr>
</table>




