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

import java.util.List;

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
	@SuppressWarnings("unchecked")
	public TextAnalyserManager() {
		System.out.println("SensiumAnalyser ctor called");
		this.google_translator = new GoogleTranslator();
		this.sensium_analyser = new SensiumAnalyser();
		
		HibernateSupport.beginTransaction();
		this.news_list = HibernateSupport.getCurrentSession().createSQLQuery(
				"SELECT * FROM News t1 "
				+ "LEFT JOIN NewsDetail t2 "
				+ "ON t1.md5_hash = t2.md5_hash "
				+ "WHERE t2.md5_hash IS NULL;")
				.addEntity(News.class).list();
		
		HibernateSupport.commitTransaction();	
		
		//this.news_list = HibernateSupport.readMoreObjects(News.class, new ArrayList<Criterion>());
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println("start of main");
		String translated_text;

		int size = this.news_list.size();
		int i = 0;
		System.out.println("news_size = " + size);
		while(this.news_list.size() > 0) {
			this.current_news = this.news_list.get(0);
			this.news_list.remove(0);
			
			if(this.current_news.getLanguage().equals("de") && 
					this.current_news.getTranslatedContent().equals("translated_content")) {
				
				System.out.println("Now translating text.");
				translated_text = this.google_translator.translateText(this.current_news.getContent());
				
				if(translated_text != null) {
					this.current_news.setTranslatedContent(translated_text);
					HibernateSupport.beginTransaction();
					this.current_news.saveToDB();
					HibernateSupport.commitTransaction();
				} else {
					System.out.println("Translating FAILED!!!");
				}
			}
			
			if(!this.current_news.containsDetailsWithSpecificAnalyser("sensium") &&
					(this.current_news.getLanguage().equals("en") ||
					!this.current_news.getTranslatedContent().equals("translated_content"))) {
				
				System.out.println("Now analysing text.");
				this.sensium_analyser.analyseText(this.current_news);
			}
			
			System.out.print("Analysed ");
		  	System.out.printf("%.2f", ((++i)/(float)size) * 100);
		  	System.out.println(" %");
			
		}
		
		return;
		
		
	}

	/**
	 * The main method.
	 *
	 * @param argv the arguments
	 */
	public static void main(String[] argv) {
		TextAnalyserManager tam = new TextAnalyserManager();
		try {
			tam.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}

	}

	
}
