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

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class GoogleTranslator.
 */
public class GoogleTranslator {

	/** The http_req_manager. */
	private HttpRequestManager http_req_manager;

	/** The data_map. */
	private Map<String, String> data_map;
	
	/** The client. */
	public String client = "p";
	
	/** The ie. */
	public String ie = "UTF-8";
	
	/** The oe. */
	public String oe = "UTF-8";
	
	/** The sl. */
	public String sl = "de";
	
	/** The tl. */
	public String tl = "en";
	
	/**
	 * Instantiates a new google translator.
	 */
	public GoogleTranslator() {
		http_req_manager = HttpRequestManager.getInstance();
		
		this.data_map = new LinkedHashMap<String, String>();
		data_map.put("client", this.client);
		data_map.put("ie", this.ie);
		data_map.put("oe", this.oe);
		data_map.put("sl", this.sl);
		data_map.put("tl", this.tl);
		
	}
	
	/**
	 * Translate text.
	 *
	 * @param text the text
	 * @return the string
	 */
	public String translateText(String text) {
		
		HttpRequester hr = http_req_manager.getCorrespondingHttpRequester("http://translate.google.com/translate_a/t");
		data_map.put("text", text);
		String json = hr.getContentWithProxy("/translate_a/t", data_map, false);
		if(json == null) {
			return null;
		}
		
		JSONArray arr;
		String tmp;
		StringBuffer result = new StringBuffer();
		JSONObject obj;
		try {
			arr = ((JSONObject)new JSONObject(json)).getJSONArray("sentences");
			for(int i = 0; i < arr.length(); i++) {
				tmp = arr.get(i).toString();
				obj = new JSONObject(tmp);
				result.append(obj.get("trans"));
			}
			
			return result.toString();
						
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}
	
}
