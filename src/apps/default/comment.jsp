<!--
  ** Snip comments display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<table width="100%" border="0" cellspacing="2" cellpadding="1">
  <c:if test="${snip.notWeblog}">
    <tr><td><span class="snip-name"><c:out value="${snip.name}"/></span></td></tr>
    <tr width="100%"><td><span class="snip-modified"><c:out value="${snip.modified}" escapeXml="false"/></span></td></tr>
  </c:if>
  <tr><td width="100%">
    <c:out value="${snip.XMLContent}" escapeXml="false" />
  </td></tr>
  <tr><td height="20"></td></tr>
  <tr><td>
    <table width="100%" border="0" cellspacing="2" cellpadding="1">
      <c:forEach items="${snip.comments.comments}" var="comment" >
        <tr>
          <td><img src="/images/comment.png"/></td>
          <td>
            <span class="comment-author"><c:out value="${comment.modified.short}" escapeXml="false" /></span>
            <s:check roles="Owner" permission="Edit" snip="${comment}">
              [<a href="/exec/edit?name=<c:out value='${comment.name}'/>">edit</a>]
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
  </td></tr>
  <tr>
  <!-- <td>Referrer: <%=request.getHeader("REFERER")%></td> -->
 </tr>
</table>




