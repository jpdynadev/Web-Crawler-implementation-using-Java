package com;

import org.jsoup.nodes.Document;

public class LinkResponse {
private String url;
private Document html;
public LinkResponse(String url, Document html) {
	super();
	this.url = url;
	this.html = html;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public Document getHtml() {
	return html;
}
public void setHtml(Document html) {
	this.html = html;
}
@Override
public String toString() {
	return "{" + "\n" 
			+ "URL: " + this.url + "," +'\n'
			+ "HTML: " + this.html + "\n" + "}";
}
}
