package Support;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Element;

public class HttpRequester {

	private String domain;
	private Response response;
	private int connection_attempts;
	
	public HttpRequester(String domain, int connection_attempts) {
		this.domain = domain;
		this.connection_attempts = connection_attempts;
	}
	
	public Element getHtmlContent(String url_extension) {
		return getContent(this.domain + url_extension);
	}
	
	public Element getHtmlContentWithNewUrl(String url) {		
		return getContent(url);
	}
	
	private Element getContent(String url) {
		Element result = null;
		int attempts = 0;
		
		while(true) {
			try {
				this.response = Jsoup.connect(url).execute();
			} catch (IOException e) {
				
				if(++attempts == this.connection_attempts) {
					System.out.println("Couldn't connect after " + attempts + " attempts to"
							+ url);
					return null;
				} else {
					continue;
				}
				
			}
			
			break;
		}
		
		
		if(!this.response.url().toString().equals(url)) {
			System.out.println("Requested url and url of response are NOT the same.");
			return null;
		}
		
		try {
			result = this.response.parse();
		} catch (IOException e) {
			System.out.println("Parsing failed.");
			return null;
		}
		
		return result;
	}
	
}
