<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration,
                 java.text.SimpleDateFormat,
                 java.text.DateFormat"%>
 <%--
  ** Localization settings.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<%
  Locale[] locales;
  Map countries = new TreeMap();
  Map languages = new TreeMap();
  locales = Locale.getAvailableLocales();
  for (int count = 0; count < locales.length; count++) {
    if(!"".equals(locales[count].getDisplayCountry())) {
      countries.put(locales[count].getDisplayCountry(), locales[count]);
    }
    languages.put(locales[count].getDisplayLanguage(), locales[count]);
  }
  pageContext.setAttribute("countries", countries.values());
  pageContext.setAttribute("languages", languages.values());
%>

<table>
  <tr>
    <td><fmt:message key="config.app.country.text"/></td>
    <td>
      <fmt:message key="config.app.country"/><br/>
      <select size="1" name="app.country">
        <c:forEach items="${countries}" var="country">
          <option value="<c:out value='${country.country}'/>" <c:if test="${newconfig.country == country.country}">selected="selected"</c:if>><c:out value="${country.displayCountry}" /></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.language.text"/></td>
    <td>
      <fmt:message key="config.app.language"/><br/>
      <select size="1" name="app.language">
        <c:forEach items="${languages}" var="language">
          <option value="<c:out value='${language.language}'/>" <c:if test="${newconfig.language == language.language}">selected="selected"</c:if>><c:out value="${language.displayLanguage}" /></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.timezone.text"/></td>
    <td>
      <fmt:message key="config.app.timezone"/><br/>
      <select size="1" name="app.timezone">
        <%
          Map timezones = new TreeMap();
          for(int count = 0; count <= 12; count++) {
            String tzString = "GMT+"+count;
            TimeZone tz = TimeZone.getTimeZone(tzString);
            timezones.put(tz.getID().substring(0, 6), tz);
          }
          for (int count = 11; count > 0; count--) {
            String tzString = "GMT-" + count;
            TimeZone tz = TimeZone.getTimeZone(tzString);
            timezones.put(tz.getID().substring(0, 6), tz);
          }
          pageContext.setAttribute("timezones", timezones);
        %>
        <c:forEach items="${timezones}" var="timezone">
          <option value="<c:out value='${timezone.value.ID}'/>" <c:if test="${newconfig.timezone == timezone.value.ID}">selected="selected"</c:if>><fmt:message key="${timezone.key}"/></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.weblogDateFormat.text"/></td>
    <td>
      <fmt:message key="config.app.weblogDateFormat"/><br/>
      <input type="text" name="app.weblogDateFormat" value="<c:out value='${newconfig.weblogDateFormat}'/>">
      <c:if test="${!empty errors['app.weblogDateFormat']}"><img src="images/attention.jpg"></c:if>
      <%
        Locale current = Locale.getDefault();
        Configuration cfg = (Configuration)pageContext.findAttribute("newconfig");
        Locale.setDefault(cfg.getLocale());
        try {
          DateFormat df = new SimpleDateFormat(cfg.getWeblogDateFormat());
          df.setTimeZone(TimeZone.getTimeZone(cfg.getTimezone()));
          pageContext.setAttribute("date", df.format(new Date()));
        } catch (Exception e) {
          pageContext.setAttribute("date", "???");
        }
        Locale.setDefault(current);
      %>
      <div class="hint">
        (<c:out value="${date}"/>) <input type="submit" name="preview" value="<fmt:message key='config.nav.preview'/>">
      </div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.geoCoordinates.text"/></td>
    <td>
      <fmt:message key="config.app.geoCoordinates"/><br/>
      <input type="text" name="app.geoCoordinates" value="<c:out value='${newconfig.geoCoordinates}'/>">
      <c:if test="${!empty errors['app.geoCoordinates']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
</table>
