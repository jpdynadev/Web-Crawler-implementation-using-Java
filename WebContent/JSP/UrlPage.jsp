<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*" %>
<%@ page import="org.jsoup.nodes.Document" %>
 <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>URLS crawled</title>
</head>
<body>
	URLS:

	<form action="webCrawler" method="post">
	<ol>
	<c:forEach items="${URL}" var="url">
	<li>
		<p style="margin-bottom:10%;">key: ${url.key}</p>
		<div style="max-height:30%; max-width:90%; overflow: hidden; position:fixed;display:inline-block;">
			<c:if test="${url.key eq urlWanted}">
				<c:out value="${url.value}" escapeXml="false"></c:out>
			</c:if>
		</div>
		<input type="radio" id="urlToFind" value="${url.key}">
		<input type="submit" value="Show URL">
	</li>
	</c:forEach> 
	</ol>
	</form>
</body>
</html>