<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

[<a href="http://www.google.com/search?q=<c:out value='${snip.name}'/>">google</a>]
[<a href="http://www.daypop.com/search?q=<c:out value='${snip.name}'/>">daypop</a>]
<s:check roles="Authenticated" permission="Edit" snip="${snip}">
  [<a href="../exec/edit?name=<c:out value='${snip.name}'/>">edit</a>]
</s:check>
<s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true">
  <span class="inactive">[edit]</span>
</s:check>
<s:check roles="Editor" snip="${snip}">
  [<a href="../exec/remove?name=<c:out value='${snip.name}'/>">zap!</a>]
</s:check>

