<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.*" %>
<%@ page import="org.jsoup.nodes.Document" %>
 <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>WebCrawler Implementation in Java by J.P Florez</title>
<link href="https://fonts.googleapis.com/css2?family=Lato:wght@400;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="styles/styles.css">
</head>
<body>
<h1 class="title">
WebCrawler Implementation in Java by J.P Florez
</h1>
<div class="description">
<h3>
This is a dynamic web application built using Java. It contains both a visual
interpretation that you are seeing now, and a txt files containing the links crawled along with their content in a json format. 
<br>
<br>
When showing content of the crawled sites, no styles are shown due to style overriding. This will be fixed in a later update. If styles/script/meta/link tags are needed, please see the txt file.
<br>
<br>
Steps that I would like to add in the future are:
<br>
<br>
Database:
<br>
Adding persistent storage instead of making request to the server for urls that have already been crawled.
<br>
Addition of styles tag for content:
<br>
	Allowing styles tag, so that content renders as it would if you visited the url
</h3>
</div>
<form name="webCralwerForm" method="get" action="webCrawler">
	<div class="search">
	<h2>Please enter URL to crawl:</h2> 
	<input type="url" name="URL" pattern="https?://.*" placeholder="https://example.com" title="https://example.com" value="${OrgiURL}" required>
	<br>
	<input class="Crawl" type="submit" value="Crawl">
	</div>
	<ol class="list">
	<c:forEach items="${URL}" var="url">
	<li>
		<p class="urlName"> URL: <a href="${url.key}">${url.key}</a></p>
		
		<button class="showURL" name="urlToFind" value="${url.key}">Show content</button>
		<c:if test="${url.key eq urlWanted}">
		<div class="content">
				<c:out value="${doc}" escapeXml="false"></c:out>	
		</div>
		</c:if>
	</li>
	</c:forEach> 
	</ol>
</form>
</body>
</html>