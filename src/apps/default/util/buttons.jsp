<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

[<a href="http://www.google.com/search?q=<c:out value='${snip.nameEncoded}'/>">google</a>]
[<a href="http://www.daypop.com/search?q=<c:out value='${snip.nameEncoded}'/>">daypop</a>]
<s:check roles="Admin">[<a href="<c:out value='${app.configuration.path}'/>/exec/remove?name=<c:out value='${snip.nameEncoded}'/>" onClick="return confirm('This will irrecoverably delete the snip <c:out value="${snip.name}"/>!\nAre you sure?');">zap!</a>]</s:check>
<s:check roles="Editor">
 <s:checkObject permission="Edit" roles="Editor" snip="${snip}" invert="true">
  [<a href="<c:out value='${app.configuration.path}'/>/exec/lock?name=<c:out value='${snip.nameEncoded}'/>">lock</a>]
 </s:checkObject>
 <s:checkObject permission="Edit" roles="Editor" snip="${snip}">
  [<a href="<c:out value='${app.configuration.path}'/>/exec/lock?name=<c:out value='${snip.nameEncoded}'/>&unlock=true">unlock</a>]
 </s:checkObject>
</s:check>
<s:check roles="Authenticated" permission="Edit" snip="${snip}">[<a href="<c:out value='${app.configuration.path}'/>/raw/<c:out value='${snip.nameEncoded}'/>">view</a>]</s:check>
<s:check roles="Authenticated" permission="Edit" snip="${snip}">[<a href="<c:out value='${app.configuration.path}'/>/exec/edit?name=<c:out value='${snip.nameEncoded}'/>">edit</a>]</s:check>
<s:check roles="Authenticated" permission="Edit" snip="${snip}" invert="true"><span class="inactive">[edit]</span></s:check>
<s:check roles="Editor"><div class="permissions"><c:out value="${snip.permissions}"/></div></s:check>

