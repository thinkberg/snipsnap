<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Editor" snip="${snip}">
  [<a href="../exec/remove?name=<c:out value='${snip.nameEncoded}'/>">zap!</a>]
</s:check>
<s:check roles="Editor">
  <s:checkObject permission="Edit" roles="Editor" snip="${snip}" invert="true">
    [<a href="../exec/lock?name=<c:out value='${snip.nameEncoded}'/>">lock</a>]
  </s:checkObject>
  <s:checkObject permission="Edit" roles="Editor" snip="${snip}">
    [<a href="../exec/lock?name=<c:out value='${snip.nameEncoded}'/>&unlock=true">unlock</a>]
  </s:checkObject>
</s:check>
[<a href="http://www.google.com/search?q=<c:out value='${snip.nameEncoded}'/>">google</a>]
[<a href="http://www.daypop.com/search?q=<c:out value='${snip.nameEncoded}'/>">daypop</a>]
<s:check roles="Authenticated" permission="Edit" snip="${snip}">
  [<a href="../exec/edit?name=<c:out value='${snip.nameEncoded}'/>">edit</a>]
</s:check>
<s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <span class="inactive">[edit]</span>
</s:check>
<!-- <s:check roles="Authenticated">
 [<a href="<c:url value='/exec/addlabel'/>">new label</a>]
</s:check>
-->
<s:check roles="Editor">
  <BR/><c:out value="${snip.permissions}"/>
</s:check>

