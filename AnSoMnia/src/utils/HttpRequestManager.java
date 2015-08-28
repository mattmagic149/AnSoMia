package utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class HttpRequestManager {
	private static final HttpRequestManager http_req_manager = new HttpRequestManager(); 
	
	private Map<String, HttpRequester> http_requester_map;
	private ReentrantLock map_mutex;
	
	public HttpRequestManager() {
		this.http_requester_map = new LinkedHashMap<String, HttpRequester>();
		this.map_mutex = new ReentrantLock();
	}
	
	public HttpRequester getCorrespondingHttpRequester(String url) {
		String domain = "http://" + url.split("/")[2];
		
		this.map_mutex.lock();
			HttpRequester requester = this.http_requester_map.get(domain);
			if(requester == null) {
				requester = this.addHttpRequester(domain);
				this.http_requester_map.put(domain, requester);
			}
		this.map_mutex.unlock();
		
		return requester;
	}
	
	private HttpRequester addHttpRequester(String domain) {
		System.out.println("HttpRequestManager is adding " + domain);
		return new HttpRequester(domain, 3, 1000);
	}
	
	public static HttpRequestManager getInstance() { 
		return http_req_manager; 
    } 

}
