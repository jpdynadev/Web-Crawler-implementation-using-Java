package com;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	private Hashtable<String, String> urls;
	private String domain;
	private String URL;
	public WebCrawler(String URL) {
		this.urls = new Hashtable<>();
		this.domain = findDomain(URL);
		this.URL = URL;
	}
	public Hashtable<String, String> findURLS(boolean log) {
		this.urls = getDomainUrls(this.URL, log);
		return this.urls;
	}
	public Hashtable<String, String> getDomainUrls(String URL, boolean log) {
		try {
			Document doc = Jsoup.connect(URL).get();
			Elements links = doc.select("a[href]");
			
			for(Element link: links) {
				String checking = link.attr("abs:href");
				/*
				 * Checking if url contains the domain, as well as making sure it is not the same as the original url
				 * also verifying that it is not an anchor, and instead a link to another page by checking if it contains #
				 */
				if(checking.contains(this.domain) && !checking.equals(URL) && !checking.contains("#")) {
					
					if(urls.get(checking) == null) {
						Document temp = null;
						try {
							temp = Jsoup.connect(checking).get();
						}catch(Exception e) {
							System.out.println("failed to crawl: " + link.baseUri());
						}
						if(temp != null) {
							/*
							 * Adding to Links.txt with all content
							 */
							if(log) {
							//Only want to log once, checking if logging for these items has already been done. 
							logger(checking, temp);
							}
							/*
							 * Removing Script/Style tag to not mess with rendering, only for serving to the client.
							 */
							temp.select("script, style, meta, link").remove();
							urls.put(checking, temp.normalise().html());
						}
						
					}
					//return when we have reached 25 links
					if(urls.size() == 25) {
						return urls;
					}
				}
			}
			
		}catch(Exception e) {
			System.out.println("Failed due to: " + e.getLocalizedMessage());
		}
		return urls;
	}
	
	public String findDomain(String URL) {
		String domain = "";
		boolean firstSlash = false;
		boolean secondSlash = false;
		for(int i = 0; i < URL.length(); i++) {
			Character current = URL.charAt(i);
			if(current == '/' && !firstSlash) {
				firstSlash = !firstSlash;
			}else
			if(current == '/' && firstSlash && !secondSlash) {
				secondSlash = !secondSlash;
			}else
			if(secondSlash && current != '.') {
				domain = domain + current;
			}else
			if(current == '.' && (URL.length() -1)- i > 3) {
				domain = "";
			}else
			if(current == '.') {
				return domain;
			}
		}
		return "";
	}
	
	public static void logger(String link, Document doc) throws IOException {
		File f = new File("/Links.txt");
		if(!f.exists()) {
			f.createNewFile();
		}
		try(PrintWriter pw = new PrintWriter(new FileOutputStream(f, true))){
			pw.append(new Timestamp(System.currentTimeMillis()) + ": " + new LinkResponse(link,doc) + '\n');
		}catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
			System.out.println("Stack trace below: ");
			e.printStackTrace();
		}
	}
//	public static void main(String[] args) {
//		WebCrawler ws = new WebCrawler("http://www.yahoo.com");
//		ws.findURLS();
//	}
}
