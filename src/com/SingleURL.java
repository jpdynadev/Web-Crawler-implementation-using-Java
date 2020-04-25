package com;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SingleURL {
	/*
	 * For single URL retrieval only
	 */
	private String URL;
	public SingleURL(String URL) {
		this.URL = URL;
	}
	public String getSingleURL() {
		Document doc = null;
		try {
			doc = Jsoup.connect(this.URL).get();
			doc.select("style, link, meta, script").remove();
			
		}catch(Exception e) {
			System.out.println("Failed due to: " + e.getLocalizedMessage());
		}
		return doc.normalise().html();
	}
}
