<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Editor" snip="${snip}">
  [<a href="../exec/remove?name=<c:out value='${snip.name}'/>">zap!</a>]
</s:check>
<s:check roles="Editor">
  <s:checkObject permission="Edit" roles="Owner" snip="${snip}" invert="true">
    [<a href="../exec/lock?name=<c:out value='${snip.name}'/>">lock</a>]
  </s:checkObject>
  <s:checkObject permission="Edit" roles="Owner" snip="${snip}">
    [<a href="../exec/lock?name=<c:out value='${snip.name}'/>&unlock=true">unlock</a>]
  </s:checkObject>
</s:check>
[<a href="http://www.google.com/search?q=<c:out value='${snip.name}'/>">google</a>]
[<a href="http://www.daypop.com/search?q=<c:out value='${snip.name}'/>">daypop</a>]
<s:check roles="Authenticated" permission="Edit" snip="${snip}">
  [<a href="../exec/edit?name=<c:out value='${snip.name}'/>">edit</a>]
</s:check>
<s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <span class="inactive">[edit]</span>
</s:check>

