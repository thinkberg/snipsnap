<%--
  ** Snip display template.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://snipsnap.com/snipsnap" prefix="s" %>

<s:check roles="Owner,Editor">
 <script language="Javascript" type="text/javascript">
 <!--
  function showHide(obj)
  {
    if (document.layers)
    {
      current = (document.layers[obj].display == 'none') ? 'block' : 'none';
      document.layers[obj].display = current;
    }
    else if (document.all)
    {
      current = (document.all[obj].style.display == 'none') ? 'block' : 'none';
      document.all[obj].style.display = current;
    }
    else if (document.getElementById)
    {
      vista = (document.getElementById(obj).style.display == 'none') ? 'block' : 'none';
      document.getElementById(obj).style.display = vista;
    }
  }
 // -->
 </script>
 <a href="#" onClick="showHide('files'); return false;">Show Attached Files</a><p/>
 <div id="files" class="snip-attachments" style="display: none">

 </div>
</s:check>