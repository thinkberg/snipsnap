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
                 org.snipsnap.snip.SnipSpace"%>
 <%--
  ** Theme selection.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
  Map themes = new HashMap();

  Configuration conf = (Configuration) session.getAttribute("newconfig");
  // add/overwrite themes already added within the SnipSpace
  if (conf.isConfigured()) {
    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
    Snip[] themeSnips = space.match("SnipSnap/themes/");
    for (int s = 0; s < themeSnips.length; s++) {
      if (themeSnips[s].getName().indexOf('/', "SnipSnap/themes/".length()) == -1) {
        String name = themeSnips[s].getName();
        themes.put(name.substring(name.lastIndexOf('/') + 1), themeSnips[s].getContent());
      }
    }
  }

  request.setAttribute("themes", themes);

  Map newThemes = new HashMap();
  // find theme files in filesystem
  File themeDir = new File(conf.getWebInfDir(), "themes");
  File[] files = themeDir.listFiles(new FilenameFilter() {
    public boolean accept(File file, String s) {
      return s.endsWith(".snip");
    }
  });

  SAXReader saxReader = new SAXReader();
  for (int f = 0; f < files.length; f++) {
    Document themeDoc = saxReader.read(new FileReader(files[f]));
    Iterator it = themeDoc.getRootElement().elementIterator("snip");
    while (it.hasNext()) {
      Element snipEl = (Element) it.next();
      String name = snipEl.element("name").getTextTrim();
      if (name.indexOf('/', "SnipSnap/themes/".length()) == -1) {
        String content = snipEl.element("content").getText();
        String themeName = name.substring(name.lastIndexOf('/') + 1);
        if(!themes.containsKey(themeName)) {
          newThemes.put(themeName, content);
        }
      }
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
            <c:if test="${newconfig.theme == theme.key}">selected="selected"</c:if>><c:if test="${newconfig.configured}"><fmt:message key="config.app.theme.new">
              <fmt:param><c:out value="${theme.key}" escapeXml="true"/></fmt:param>
            </fmt:message></c:if></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <c:forEach items="${themes}" var="theme">
    <tr id="<c:out value='${theme.key}'/>" name="theme"
      <c:if test="${newconfig.theme != theme.key}">style="visibility:hidden; display:none"</c:if>>
      <td><c:out value="${theme.value}"/></td>
      <td>
        <c:out value="${theme.key}"/><br/>
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
