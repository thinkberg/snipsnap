<%@ page import="java.util.*"%>
 <%--
  ** Localization settings.
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<% pageContext.setAttribute("locales", Locale.getAvailableLocales()); %>

<table>
  <tr><th colspan="2"><fmt:message key="admin.config.step.localization"/></th></tr>
  <tr>
    <td><fmt:message key="admin.config.app.country.text"/></td>
    <td>
      <fmt:message key="admin.config.app.country"/><br/>
      <select size="1" name="app.country">
        <c:forEach items="${locales}" var="locale">
          <c:choose>
            <c:when test="${empty locale.country}">
              <option>------------</option>
            </c:when>
            <c:otherwise>
              <option value="<c:out value='${locale.country}'/>" <c:if test="${config.country == locale.country}">selected="selected"</c:if>><c:out value="${locale.displayCountry}" /></option>
            </c:otherwise>
          </c:choose>
        </c:forEach>
      </select>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.language.text"/></td>
    <td>
      <fmt:message key="admin.config.app.language"/><br/>
      <select size="1" name="app.language">
        <%
          Map languages = new TreeMap();
          Locale[] locales = Locale.getAvailableLocales();
          for(int count = 0; count < locales.length; count++) {
            languages.put(locales[count].getLanguage(), locales[count]);
          }
          pageContext.setAttribute("languages", languages);
        %>
        <c:forEach items="${languages}" var="language">
          <option value="<c:out value='${language.key}'/>" <c:if test="${config.language == language.key}">selected="selected"</c:if>><c:out value="${language.value.displayLanguage}" /></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.timezone.text"/></td>
    <td>
      <fmt:message key="admin.config.app.timezone"/><br/>
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
          <option value="<c:out value='${timezone.value.ID}'/>" <c:if test="${config.timezone == timezone.value.ID}">selected="selected"</c:if>><fmt:message key="${timezone.key}"/></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="admin.config.app.geoCoordinates.text"/></td>
    <td>
      <fmt:message key="admin.config.app.geoCoordinates"/><br/>
      <input type="text" value="<c:out value='${config.geoCoordinates}'/>">
    </td>
  </tr>
</table>
