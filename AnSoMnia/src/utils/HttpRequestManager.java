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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.javatuples.Pair;

/**
 * The Class HttpRequestManager.
 */
public class HttpRequestManager {
	
	/** The Constant http_req_manager. */
	private static final HttpRequestManager http_req_manager = new HttpRequestManager(); 
	
	/** The http_requester_map. */
	private Map<String, HttpRequester> http_requester_map;
	
	/** The map_mutex. */
	private ReentrantLock map_mutex;
	
	/** The proxy_file_name. */
	private String proxy_file_name;
	
	/** The proxy_list. */
	private ArrayList<Pair<String, Integer>> proxy_list;
	
	/** The user_agents. */
	private ArrayList<String> user_agents;
	
	/** The random_gen. */
	private Random random_gen;
	
	/**
	 * Instantiates a new http request manager.
	 */
	public HttpRequestManager() {
		System.out.println("HttpRequestManager ctor called...");
		
		this.random_gen = new Random();
		this.proxy_file_name = "data/proxies.csv";
		this.http_requester_map = new LinkedHashMap<String, HttpRequester>();
		this.map_mutex = new ReentrantLock();
		this.downloadProxyList();
		this.initializeProxyList();
		this.initializeUserAgentsList();
	}
	
	/**
	 * Gets the random user agent.
	 *
	 * @return the random user agent
	 */
	public String getRandomUserAgent() {
		int random = this.random_gen.nextInt(this.user_agents.size());
		return this.user_agents.get(random);
	}
	
	/**
	 * Gets the random proxy.
	 *
	 * @return the random proxy
	 */
	public Pair<String, Integer> getRandomProxy() {
		int random = this.random_gen.nextInt(this.proxy_list.size());
		return this.proxy_list.get(random);
	}
	
	/**
	 * Gets the corresponding http requester.
	 *
	 * @param url the url
	 * @return the corresponding http requester
	 */
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
	
	/**
	 * Adds the http requester.
	 *
	 * @param domain the domain
	 * @return the http requester
	 */
	private HttpRequester addHttpRequester(String domain) {
		System.out.println("HttpRequestManager is adding " + domain);
		return new HttpRequester(this, domain, 3, 1000);
	}
	
	/**
	 * Download proxy list.
	 *
	 * @return true, if successful
	 */
	private boolean downloadProxyList() {
		URL website;
		try {
			website = new URL("http://txt.proxyspy.net/proxy.txt");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(proxy_file_name);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * Initialize proxy list.
	 *
	 * @return true, if successful
	 */
	private boolean initializeProxyList() {
		ArrayList<String> tmp = new ArrayList<String>();
		this.proxy_list = new ArrayList<Pair<String, Integer>>();

		FileReader file_reader;
		BufferedReader buffered_reader = null;
		try {
			file_reader = new FileReader(proxy_file_name);
			buffered_reader = new BufferedReader(file_reader);
			String line = null;
			while ((line = buffered_reader.readLine()) != null) {
				tmp.add(line);
			}
			buffered_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		String proxy_string;
		String[] proxy_port;
		String[] proxy_info;
		Pair<String, Integer> proxy = null;
		
		//we don't want to read the first two and last two lines
		for(int i = 2; i < tmp.size() - 2; i++) {
			proxy_info = tmp.get(i).split(" ");
			proxy_string = proxy_info[0];
			proxy_port = proxy_string.split(":");
			if(proxy_port.length == 2 && proxy_info.length == 3 && proxy_info[2].equals("+")) {
				proxy = new Pair<String, Integer>(proxy_port[0], Integer.parseInt(proxy_port[1]));
				this.proxy_list.add(proxy);
			}
		}
		
		for(int i = 0; i < this.proxy_list.size(); i++) {
			System.out.println(this.proxy_list.get(i).getValue0() + " " + this.proxy_list.get(i).getValue1());
		}
		
		assert(this.proxy_list.size() >= 10);
		
		return true;
	}
	
	/**
	 * Initialize user agents list.
	 *
	 * @return true, if successful
	 */
	private boolean initializeUserAgentsList() {
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
		
		assert(this.user_agents.size() >= 10);
		
		return true;	
	}
	
	/**
	 * Gets the single instance of HttpRequestManager.
	 *
	 * @return single instance of HttpRequestManager
	 */
	public static HttpRequestManager getInstance() { 
		return http_req_manager; 
    } 

	
	
}
