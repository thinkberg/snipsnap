<!--
  ** weblog post template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Editor">
 <h1 class="header">Post To Weblog</h1>
 <form name="f" method="POST" action="../exec/storepost">
   <table border="0" cellpadding="0" cellspacing="2">
     <tr><td><textarea name="content" type="text" cols="80" rows="20"></textarea></td></tr>
     <tr><td align="right">
       <input value="Post" name="save" type="submit">
       <input value="Cancel" name="cancel" type="submit">
     </td></tr>
   </table>
  <input name="post" type="hidden" value="weblog">
  <input name="referer" type="hidden" value="<%= request.getHeader("REFERER") %>">
 </form>
</s:check>

<s:check roles="Editor" invert="true">
 Please <a href="../exec/login.jsp">login!</a> as editor.
</s:check>
