<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration,
                 java.io.File,
                 org.dom4j.Document,
                 org.dom4j.io.SAXReader,
                 java.io.FileReader,
                 java.io.InputStreamReader,
                 org.dom4j.Element,
                 org.dom4j.Node,
                 java.io.FilenameFilter,
                 org.snipsnap.snip.Snip,
                 org.snipsnap.container.Components,
                 org.snipsnap.snip.SnipSpace,
                 org.snipsnap.net.admin.ThemeHelper"%>
 <%--
  ** Theme selection.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
  Configuration conf = (Configuration) session.getAttribute("newconfig");
  Map themes = new HashMap();
  if (conf.isConfigured()) {
    Map installedThemes = ThemeHelper.getInstalledThemes();
    Iterator themeIt = installedThemes.keySet().iterator();
    while (themeIt.hasNext()) {
      String themeName = (String) themeIt.next();
      themes.put(themeName, ((Snip)installedThemes.get(themeName)).getContent());
    }
  }
  request.setAttribute("themes", themes);
  Map newThemes = ThemeHelper.getThemeDocuments(conf, ThemeHelper.CONTENT);
  Iterator instThemeIt = themes.keySet().iterator();
  while (instThemeIt.hasNext()) {
    String themeName = (String) instThemeIt.next();
    if(newThemes.containsKey(themeName)) {
      newThemes.remove(themeName);
    }
  }
  request.setAttribute("newthemes", newThemes);
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
          <option value="<c:out value='${theme.key}'/>"
            <c:if test="${newconfig.theme == theme.key}">selected="selected"</c:if>><c:out value="${theme.key}"/></option>
        </c:forEach>
        <c:forEach items="${newthemes}" var="theme">
          <option value="<c:out value='${theme.key}'/>"
            <c:if test="${newconfig.theme == theme.key}">selected="selected"</c:if>><fmt:message key="config.app.theme.new">
              <fmt:param><c:out value="${theme.key}" escapeXml="true"/></fmt:param>
            </fmt:message></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <c:forEach items="${themes}" var="theme">
    <tr id="<c:out value='${theme.key}'/>" name="theme"
      <c:if test="${newconfig.theme != theme.key}">style="visibility:hidden; display:none"</c:if>>
      <td><c:out value="${theme.value}"/></td>
      <td>
        <c:out value="${theme.key}"/> <input type="submit" name="export" value="<fmt:message key='config.app.theme.export'/>"/><br/>
        <img src="themeimage?name=<c:out value='${theme.key}'/>" alt="<c:out value='${theme.key}'/>" border="0">
      </td>
    </tr>
  </c:forEach>
    <c:forEach items="${newthemes}" var="theme">
    <tr id="<c:out value='${theme.key}'/>" name="theme"
      <c:if test="${newconfig.theme != theme.key}">style="visibility:hidden; display:none"</c:if>>
      <td><c:out value="${theme.value}"/></td>
      <td>
        <c:out value="${theme.key}"/><br/>
        <img src="themeimage?name=<c:out value='${theme.key}'/>" alt="<c:out value='${theme.key}'/>" border="0">
      </td>
    </tr>
  </c:forEach>
</table>
