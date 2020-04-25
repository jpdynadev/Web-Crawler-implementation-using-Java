package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.SingleURL;
import com.WebCrawler;

@WebServlet("/webCrawler")
public class WebCrawlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    //DoGet method to return URLs with content 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String URL = request.getParameter("URL");
		boolean log = true;
		
		try {
			if(!(request.getParameter("urlToFind").isEmpty())){
				String url = request.getParameter("urlToFind");
				log = false;
				SingleURL su = new SingleURL(url);
				String doc = su.getSingleURL();
				request.setAttribute("urlWanted", url);
				request.setAttribute("doc", doc);
			}
			
		}catch(Exception e) {
			System.out.println("Failed due to: " + e.getLocalizedMessage());
		}
		
		request.setAttribute("OrgiURL", URL);
		WebCrawler ws = new WebCrawler(URL);
		Hashtable<String, String> urls = ws.findURLS(log);
		request.setAttribute("URL", urls);
		
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
		
	}
}
