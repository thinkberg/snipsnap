<jsp:useBean id="snip" scope="request" class="com.neotis.jsp.SnipBean">
  <jsp:setProperty name="snip" property="*"/>
</jsp:useBean>

<h1 class="header"><jsp:getProperty name="snip" property="name" /></h1>
<form method="POST" action="/exec/store">
 <input name="name" type="hidden" value="<%= snip.getName() %>">
 <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
 <br>
 <textarea name="content" type="text" cols="80" rows="20"><jsp:getProperty name="snip" property="content" /></textarea><br/>
 <input value="Cancel" name="cancel" type="submit">
 <input value="Save" name="save" type="submit">
</form>
