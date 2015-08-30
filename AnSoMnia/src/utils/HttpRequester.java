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
package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.javatuples.Pair;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * The Class HttpRequester.
 */
public class HttpRequester {
	
	/**
	 * The Enum ReturnType.
	 */
	public enum ReturnType {	    
    	HTML, 
    	JSON_STRING 
	}

	/** The hrm. */
	private HttpRequestManager hrm;

	/** The domain. */
	private String domain;
	
	/** The response. */
	private Response response;
	
	/** The connection_attempts. */
	private int connection_attempts;
	
	/** The average_timeout. */
	private int average_timeout;
	
	/** The timeout_sigma. */
	private int timeout_sigma;
	
	/** The random_gen. */
	private Random random_gen;
	
	/** The request_timeout. */
	private int request_timeout;
	
	/** The last_request. */
	private Date last_request;
	
	/** The mutex. */
	private ReentrantLock mutex;
	
	
	/**
	 * Instantiates a new http requester.
	 *
	 * @param hrm the hrm
	 * @param domain the domain
	 * @param connection_attempts the connection_attempts
	 * @param average_timeout the average_timeout
	 */
	public HttpRequester(HttpRequestManager hrm, String domain, int connection_attempts, int average_timeout) {
		this.hrm = hrm;
		
		this.random_gen = new Random();
		this.request_timeout = 3000;
		this.mutex = new ReentrantLock();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -30);
		this.last_request = calendar.getTime();
		
		this.domain = domain;
		this.connection_attempts = connection_attempts;
		this.average_timeout = average_timeout;
		this.timeout_sigma = 100;
		
	}
	
	/**
	 * Gets the html content.
	 *
	 * @param url_extension the url_extension
	 * @return the html content
	 */
	public Element getHtmlContent(String url_extension) {
		return getContentWithJsoup(this.domain + url_extension, HttpRequester.ReturnType.HTML);
	}
	
	/**
	 * Gets the html content with complete url.
	 *
	 * @param url the url
	 * @return the html content with complete url
	 */
	public Element getHtmlContentWithCompleteUrl(String url) {		
		return getContentWithJsoup(url, HttpRequester.ReturnType.HTML);
	}
	
	/**
	 * Gets the json content.
	 *
	 * @param url_extension the url_extension
	 * @return the json content
	 */
	public String getJsonContent(String url_extension) {
		return getContentWithJsoup(this.domain + url_extension, HttpRequester.ReturnType.JSON_STRING);
	}
	
	/**
	 * Gets the content with proxy.
	 *
	 * @param url_extension the url_extension
	 * @param params the params
	 * @param use_proxy the use_proxy
	 * @return the content with proxy
	 */
	public String getContentWithProxy(String url_extension, Map<String, String> params, boolean use_proxy) {
		return getContentWithHttpConnector(this.domain + url_extension, params, use_proxy);
	}
	
	/**
	 * Gets the millis via gauss.
	 *
	 * @return the millis via gauss
	 */
	private int getMillisViaGauss() {
		double val = this.random_gen.nextGaussian() * (this.timeout_sigma) + (this.average_timeout);
		return (int) Math.round(val);
	}
	
	/**
	 * Gets the content with http connector.
	 *
	 * @param url the url
	 * @param params the params
	 * @param use_proxy the use_proxy
	 * @return the content with http connector
	 */
	private String getContentWithHttpConnector(String url, Map<String, String> params, boolean use_proxy) {
		this.mutex.lock();
		
			int attempts = 0;
			long duration;
			int new_timeout;
			
			URL requested_url;
			try {
				requested_url = new URL(url);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
				return null;
			}
			
			Pair<String, Integer> rand_proxy;
			String user_agent;
			Proxy proxy;
			Document result;
			HttpURLConnection con;
			
			while(true) {
				
				//ensure that requests are not fired too rapidly.
				new_timeout = this.getMillisViaGauss();
				duration = new Date().getTime() - this.last_request.getTime();
				if(duration < new_timeout) {
					try {
						Thread.sleep(new_timeout - duration);
					} catch (InterruptedException e) {
						e.printStackTrace();
						assert(false);
					}
				}
				
				try {
					this.last_request = new Date();
					user_agent = this.hrm.getRandomUserAgent();
					
					if(use_proxy) {
						rand_proxy = this.hrm.getRandomProxy();
						System.out.println(rand_proxy.getValue0());
						proxy = new Proxy(Proxy.Type.HTTP, 
										  new InetSocketAddress(
												  rand_proxy.getValue0(), 
												  rand_proxy.getValue1()));
						con = (HttpURLConnection) requested_url.openConnection(proxy);
					} else {
						con = (HttpURLConnection) requested_url.openConnection();
					}
					
					con.setRequestProperty("User-Agent", user_agent);
					con.setRequestProperty("Referer", "http://www.google.com");
					con.setRequestMethod("POST");
					//con.setConnectTimeout(this.request_timeout);
					
					StringBuffer requestParams = new StringBuffer();
					 
			        if (params != null && params.size() > 0) {
			 
			            con.setDoOutput(true); // true indicates POST request
			 
			            // creates the params string, encode them using URLEncoder
			            Iterator<String> paramIterator = params.keySet().iterator();
			            while (paramIterator.hasNext()) {
			                String key = paramIterator.next();
			                String value = params.get(key);
			                requestParams.append(URLEncoder.encode(key, "UTF-8"));
			                requestParams.append("=").append(
			                        URLEncoder.encode(value, "UTF-8"));
			                requestParams.append("&");
			            }
			 
			            // sends POST data
			            OutputStreamWriter writer = new OutputStreamWriter(
			                    con.getOutputStream());
			            writer.write(requestParams.toString());
			            writer.flush();
			        }
					
					if(con.getResponseCode() != HttpURLConnection.HTTP_OK) {
						System.out.println("The requested url did NOT return statusCode 2xx.");
						return null;
					}
					
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String line;
					StringBuffer response = new StringBuffer();

					while ((line = in.readLine()) != null) {
						response.append(line);
					}
					in.close();
					
					result = Jsoup.parse(response.toString());
					
				} catch (IOException e) {
					
					if(++attempts == this.connection_attempts) {
						System.out.println("Couldn't connect after " + attempts + " attempts to "
								+ url);
						this.mutex.unlock();
						return null;
					} else {
						continue;
					}
					
				}
				
				break;
			}
		
		this.mutex.unlock();
		return result.body().html();
	}
	
	/**
	 * Gets the content with jsoup.
	 *
	 * @param <T> the generic type
	 * @param url the url
	 * @param type the type
	 * @return the content with jsoup
	 */
	@SuppressWarnings("unchecked")
	private <T> T getContentWithJsoup(String url, HttpRequester.ReturnType type) {
		this.mutex.lock();
		
			T result = null;
			int attempts = 0;
			long duration;
			int new_timeout;
			while(true) {
				
				//ensure that requests are not fired too rapidly.
				new_timeout = this.getMillisViaGauss();
				duration = new Date().getTime() - this.last_request.getTime();
				if(duration < new_timeout) {
					try {
						Thread.sleep(new_timeout - duration);
					} catch (InterruptedException e) {
						e.printStackTrace();
						assert(false);
					}
				}
				
				try {
					this.last_request = new Date();
					this.response = Jsoup.connect(url)
							.ignoreContentType(true)
					        .userAgent(this.hrm.getRandomUserAgent())  
					        .referrer("http://www.google.com")
					        .timeout(this.request_timeout)
					        .execute();
					
				} catch (IOException e) {
					
					if(++attempts == this.connection_attempts) {
						System.out.println("Couldn't connect after " + attempts + " attempts to "
								+ url);
						this.mutex.unlock();
						return null;
					} else {
						continue;
					}
					
				}
				
				break;
			}

			if(this.response.statusCode() < 200 || this.response.statusCode() >= 300) {
				System.out.println("The requested url did NOT return statusCode 2xx.");
				this.mutex.unlock();
				return null;
			}
			
			try {
				if(type.equals(HttpRequester.ReturnType.HTML)) {
					result = (T) this.response.parse();
				} else if(type.equals(HttpRequester.ReturnType.JSON_STRING)) {
					result = (T) this.response.body();
				}
			} catch (IOException e) {
				System.out.println("Parsing failed.");
				this.mutex.unlock();
				return null;
			}
		
		this.mutex.unlock();
		return result;
	}
	
}
