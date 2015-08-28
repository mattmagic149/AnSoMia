package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Element;

public class HttpRequester {
	
	public enum ReturnType {
	    HTML, JSON_STRING 
	}

	private String domain;
	private Response response;
	private int connection_attempts;
	private ArrayList<String> user_agents;
	private int average_timeout;
	private int timeout_sigma;
	private Random random_gen;
	private int request_timeout;
	private Date last_request;
	private ReentrantLock mutex;
	
	public HttpRequester(String domain, int connection_attempts, int average_timeout) {
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
		this.user_agents = new ArrayList<String>();
		FileReader file_reader;
		BufferedReader buffered_reader;
		try {
			file_reader = new FileReader("data/user_agents.csv");
			buffered_reader = new BufferedReader(file_reader);
			String line;
			while ((line = buffered_reader.readLine()) != null) {
				this.user_agents.add(line);
			}
			buffered_reader.close();
		} catch (IOException e) {
			System.out.println("couldn't load data/headers.csv in HttpRequester ctor.");
			assert(false);
		}
		
	}
	
	public Element getHtmlContent(String url_extension) {
		return getContent(this.domain + url_extension, HttpRequester.ReturnType.HTML);
	}
	
	public Element getHtmlContentWithCompleteUrl(String url) {		
		return getContent(url, HttpRequester.ReturnType.HTML);
	}
	
	public String getJsonContent(String url_extension) {
		return getContent(this.domain + url_extension, HttpRequester.ReturnType.JSON_STRING);
	}
	
	private int getMillisViaGauss() {
		double val = this.random_gen.nextGaussian() * (this.timeout_sigma) + (this.average_timeout);
		return (int) Math.round(val);
	}
	
	private String getRandomUserAgent() {
		int random = this.random_gen.nextInt(this.user_agents.size());
		return this.user_agents.get(random);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getContent(String url, HttpRequester.ReturnType type) {
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
					        .userAgent(this.getRandomUserAgent())  
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
