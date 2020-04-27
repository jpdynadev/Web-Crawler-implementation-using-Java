# Web-Crawler-implementation-using-Java
Web Crawler implementation using Java

This is a web crawler implementation maven project using JSP for front end. 

The project is broken down into three major components:

    1. index.jsp <- Handles all front end logic 
    2. WebCrawlerServlet.java <- server side logic for processing requests and serving response to client
    3. WebCrawler.java/SingleURL.java <- object and class files that process data and return data to servlet
    
Starting with index.jsp
### Front-End
To keep things clean and simple, we are using a single form. Next implementation of this will contain more jsp pages to seperate front end logic, but for our purposes one form will be enough.

```
<form name="webCralwerForm" method="get" action="webCrawler">
```
The form contains the http protocol we will use to access our servlet, as well as the mapping for our servlet "webCrawler" which is indicated in our deployment descriptor.


```
	<div class="search">
	<h2>Please enter URL to crawl:</h2> 
	<input type="url" name="URL" pattern="https?://.*" placeholder="https://example.com" title="https://example.com" value="${OrgiURL}" required>
	<br>
	<input class="Crawl" type="submit" value="Crawl">
	</div>  
```
This next part of the form is our search field. 
URL validation is done on the front end to allow for user reaction immediately if a mistake is made, instead of waiting for server to return response. 
It will also prompt the user to enter a valid url if one is not given:

![image](https://user-images.githubusercontent.com/59784335/80270377-5f8bef00-8685-11ea-869b-46650ac4079e.png)


```
	<ol class="list">
	<c:forEach items="${URL}" var="url">
```
We are using JSTL to parse our hash table, notice that this section of the form will not appear unless the request contains a "URL" attribute

```
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
```

Inside each list item there is a URL name, containing the link name, wrapped in an a tag to allow users to visit site directly.
There is also a "Show Content" button that will load the html content of the link inside the list item. Unfortunately, currently no styles for the 
mentioned html content will be shown inside the list item due to style overriding, this will be fixed in a future implementation. 

![image](https://user-images.githubusercontent.com/59784335/80270449-3455cf80-8686-11ea-99d9-27237edab821.png)

The full content of the html, including style, script, meta, link tags can be viewed in the Link.txt file created. The data is stored in json format to allow for easy parsing.

### Server Side

Next is the WebCrawlerServlet that process our request from the jsp

```
@WebServlet("/webCrawler")
public class WebCrawlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    //DoGet method to return URLs with content 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String URL = request.getParameter("URL");
		boolean log = true;
    
```
Here we retrieve the URL to crawl, along with setting our logger to true in case this is the first time retrieving this url. 


```
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
```
If we have already served the response and the request contains a parameter called "urlTofind", we will call the SingleURL class to retrieve the html from this one url.

Future implementation will include pulling and storing information from DB for urls that have already been requested, saving time for the server to process the request.

```
		request.setAttribute("OrgiURL", URL);
		WebCrawler ws = new WebCrawler(URL);
		Hashtable<String, String> urls = ws.findURLS(log);
		request.setAttribute("URL", urls);
		
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
		rd.forward(request, response);
		
	}
}
```
Finally the servlet sets the map into the attribute "URL" for the JSTL to iterate through and create the list items.
Then the request is directed back to the jsp, this could also be redirected to a seperate jsp if abstraction of user flow was needed. 


### WebCrawler.java

This object contains three attributes:
      1. Hashtable<String, String> for storing and accessing our list and their content
      2. String domain, finding the root domain of the url we are crawling
      3. String URL, the original URL we crawl.
      
 The constructor looks like the following: 
 
 ```
 	public WebCrawler(String URL) {
		this.urls = new Hashtable<>();
		this.domain = findDomain(URL);
		this.URL = URL;
	}
  ```
  
  The main part of this class is the getDomainUrls:
  
  We are using Jsoup to parse and retrieve our html content.
  
  ```
  try {
			Document doc = Jsoup.connect(URL).get();
			Elements links = doc.select("a[href]");
			
			for(Element link: links) {
				String checking = link.attr("abs:href");
   ```
   
   Here we create a Document element and connect to our url, then we select all the a[href] tags to retrieve our links. 
   
   For every element in the links array that we get from our original url, we will get the link url attr that the href was pointing too.
   
   ```
   if(checking.contains(this.domain) && !checking.equals(URL) && !checking.contains("#")) {
   ```
   
   If the url contains our root domain, we will continue to process the data:
   
   ```
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
   ```
   
  If our hashtable does not contain the url we are currently parsing, we will retrieve the html content, store it in our text file, extract the style, script, link, and meta tags, and then add it to our hashtable.
  
  Once we have collected 25 urls, we will return the request, otherwise we will return the amount that were on the page less than 25. 
  
  In order to find the root domain, a helper method was made:
  
```
	public String findDomain(String URL) {
		try {
			String domain = InternetDomainName.from(new URI(URL).getHost()).topPrivateDomain().toString();
			return domain.substring(0, domain.indexOf('.'));
		}catch(Exception e) {
			System.out.println("Domain could not be found");
			return "";
		}
	}
```

  
We create a URI object from java.net and add the url string retrieved from the servlet. Then we find the substring until the '.' character to get the root domain of our url. 


```
	public static void logger(String link, Document doc) {
		try(PrintWriter pw = new PrintWriter(new FileOutputStream("/Links.txt", true))){
			pw.append(new Timestamp(System.currentTimeMillis()) + ": " + new LinkResponse(link,doc) + '\n');
		}catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
			System.out.println("Stack trace below: ");
			e.printStackTrace();
		}
	}
```

Simple logger to store our links and content in a Links.txt file.

###SingleURL.java

POJO with ability to retrieve content from a single url using Jsoup.


```
public String getSingleURL() {
		Document doc = null;
		try {
			doc = Jsoup.connect(this.URL).get();
			doc.select("style, link, meta, script").remove();
			//Removing styles tag as this method is only called when user has clicked "Show Content" therefore, the original html has already
      //been added to the Links.txt with all original tags
		}catch(Exception e) {
			System.out.println("Failed due to: " + e.getLocalizedMessage());
		}
		return doc.normalise().html();
	}
```

###Design

Landing Page:

![image](https://user-images.githubusercontent.com/59784335/80375509-6f165e00-8866-11ea-86bc-6711410f94d2.png)


Pulling Search Results:

![image](https://user-images.githubusercontent.com/59784335/80376189-74c07380-8867-11ea-84bf-7fb0e77951e5.png)


Showing individual URL content:

![image](https://user-images.githubusercontent.com/59784335/80375739-c4526f80-8866-11ea-90d4-adceb731fefb.png)
