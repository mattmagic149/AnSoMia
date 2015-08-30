package test;

import org.jsoup.nodes.Element;

import utils.HttpRequestManager;
import utils.HttpRequester;


public class TestClassThread extends Thread {
	
	private int id;
	private String url_to_request;
	private HttpRequestManager http_req_manager;
	
	public TestClassThread(String url, int id) {
		this.id = id;
		this.url_to_request = url;
		this.http_req_manager = HttpRequestManager.getInstance();
	}
	
	public void run() {
        System.out.println("Hello I am thread " + this.id + "! I will request: " + this.url_to_request);
        
        
        HttpRequester http_requester = this.http_req_manager
        								.getCorrespondingHttpRequester(this.url_to_request);
        
        
        System.out.println("Thread: " + this.id + " got HttpRequester.");
        
        @SuppressWarnings("unused")
		Element element = http_requester.getHtmlContent("");
        
        System.out.println("Thread: " + this.id + " is done.");
    }
    
}
