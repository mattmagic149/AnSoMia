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
package analysers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import utils.GoogleTranslator;
import utils.HibernateSupport;
import database.*;

// TODO: Auto-generated Javadoc
/**
 * The Class TextAnalyserManager.
 */
public class TextAnalyserManager implements Job
{
	
	/** The google_translator. */
	private GoogleTranslator google_translator;
	
	/** The sensium_analyser. */
	private SensiumAnalyser sensium_analyser;
	
	/** The news_list. */
	private List<News> news_list;
	
	/** The current_news. */
	private News current_news;
	
	/**
	 * Instantiates a new text analyser manager.
	 */
	public TextAnalyserManager() {
		System.out.println("SensiumAnalyser ctor called");
		this.google_translator = new GoogleTranslator();
		this.sensium_analyser = new SensiumAnalyser();
		
		this.news_list = HibernateSupport.readMoreObjects(News.class, new ArrayList<Criterion>());
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		int size = this.news_list.size();
		for(int i = 0; i < size; i++) {
			this.current_news = this.news_list.get(i);
			
			if(!this.current_news.containsDetailsWithSpecificAnalyser("sensium") &&
					(this.current_news.getLanguage().equals("en") ||
					!this.current_news.getTranslatedContent().equals("translated_content"))) {
				this.sensium_analyser.analyseText(this.current_news);
			}
			
			System.out.print("Crawled ");
		  	System.out.printf("%.2f", (i/(float)size) * 100);
		  	System.out.println(" % - " + (size) + " not crawled");	
			
		}
		
		
	}

	/**
	 * The main method.
	 *
	 * @param argv the arguments
	 */
	public static void main(String[] argv) {
		System.out.println("start of main");
		TextAnalyserManager tam = new TextAnalyserManager();
		String translated_text;

		int size = tam.news_list.size();
		for(int i = 0; i < size; i++) {
			tam.current_news = tam.news_list.get(i);
			
			if(tam.current_news.getLanguage().equals("de") && 
				tam.current_news.getTranslatedContent().equals("translated_content")) {
				
				System.out.println("Now translating text.");
				translated_text = tam.google_translator.translateText(tam.current_news.getContent());
				
				if(translated_text != null) {
					tam.current_news.setTranslatedContent(translated_text);
					HibernateSupport.beginTransaction();
					tam.current_news.saveToDB();
					HibernateSupport.commitTransaction();
				} else {
					System.out.println("Translating FAILED!!!");
				}
			}
			
			if(!tam.current_news.containsDetailsWithSpecificAnalyser("sensium") &&
					(tam.current_news.getLanguage().equals("en") ||
					!tam.current_news.getTranslatedContent().equals("translated_content"))) {
				
				System.out.println("Now analysing text.");
				tam.sensium_analyser.analyseText(tam.current_news);
			}
			
			System.out.print("Analysed ");
		  	System.out.printf("%.2f", (i/(float)size) * 100);
		  	System.out.println(" %");
			
		}

	}

	
}
