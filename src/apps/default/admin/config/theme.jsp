<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration,
                 java.io.File,
                 org.snipsnap.config.theme.Theme"%>
 <%--
  ** Database Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
  // put known drivers in session context
  if(null == session.getAttribute("themes")) {
    Configuration conf = (Configuration) session.getAttribute("newconfig");
    Collection themes = new ArrayList();
    File themeDir = new File(conf.getWebInfDir(), "themes");
    File[] files = themeDir.listFiles();
    for(int f = 0; f < files.length; f++) {
      Theme theme = new Theme(files[f]);
      if(null != theme.getName()) {
        themes.add(theme);
      }
    }
    session.setAttribute("themes", themes);
  }
%>

<script type="text/javascript" language="Javascript">
  <!--
  function selectOptions(select) {
    var themes = document.getElementsByName("theme");
    hideParts(themes);
    showPart(document.getElementById(select.options[select.selectedIndex].value));
  }

  function showPart(element) {
    element.style.visibility = "visible";
    element.style.display = "table-row";
  }

  function hideParts(elements) {
    // iterator over elements and
    for(var e = 0; e < elements.length; e++) {
      elements[e].style.visibility = "hidden";
      elements[e].style.display = "none";
    }
  }
  -->
</script>

<table>
  <tr>
    <td><fmt:message key="config.app.theme.text"/></td>
    <td>
      <fmt:message key="config.app.theme"/><br/>
      <select name="app.theme" size="1" onChange="selectOptions(this)">
        <c:forEach items="${themes}" var="theme">
          <option value="<c:out value='${theme.name}'/>"
            <c:if test="${newconfig.theme == theme.name}">selected="selected"</c:if>><c:out value="${theme.name}"/></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <c:forEach items="${themes}" var="theme">
    <tr id="<c:out value='${theme.name}'/>" name="theme"
      <c:if test="${newconfig.theme != theme.name}">style="visibility:hidden; display:none"</c:if>>
      <td><c:out value="${theme.description}"/></td>
      <td>
        <c:out value="${theme.name}"/><br/>
        <img src="themeimage?name=<c:out value='${theme.name}'/>" alt="<c:out value='${themename}'/>" border="0">
      </td>
    </tr>
  </c:forEach>
</table>
