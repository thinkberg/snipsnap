<!--
  ** Display information after the installation is finished.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${config.configured}">
    <h1>Your SnipSnap Installation finished sucessfully!</h1>
    Point your browser to the following address:
    <ul>
      <li><a href="http://<c:out value='${config.host}' default='localhost'/>:<c:out value='${config.port}${config.contextPath}'/>">
       http://<c:out value='${config.host}' default="localhost"/>:<c:out value='${config.port}${config.contextPath}'/>
      </a>
    </ul>
    <b>Enjoy your SnipSnap!</b>
  </c:when>
  <c:otherwise>
    Your SnipSnap Installation is not configured, please <a href="../">do so</a>!
  </c:otherwise>
</c:choose>