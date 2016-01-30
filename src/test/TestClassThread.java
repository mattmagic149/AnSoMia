/*
 * @Author: Matthias Ivantsits
 * Supported by TU-Graz (KTI)
 * 
 * Tool, to gather market information, in quantitative and qualitative manner.
 * Copyright (C) 2015  Matthias Ivantsits
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package test;

import org.jsoup.nodes.Element;

import utils.HttpRequestManager;
import utils.HttpRequester;


// TODO: Auto-generated Javadoc
/**
 * The Class TestClassThread.
 */
public class TestClassThread extends Thread {
	
	/** The id. */
	private int id;
	
	/** The url_to_request. */
	private String url_to_request;
	
	/** The http_req_manager. */
	private HttpRequestManager http_req_manager;
	
	/**
	 * Instantiates a new test class thread.
	 *
	 * @param url the url
	 * @param id the id
	 */
	public TestClassThread(String url, int id) {
		this.id = id;
		this.url_to_request = url;
		this.http_req_manager = HttpRequestManager.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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
