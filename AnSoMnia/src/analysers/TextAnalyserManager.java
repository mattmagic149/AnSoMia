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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import charts.NewsHistogram;
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
	
	public void createNumberOfNewsPerCompanyHist() {
		List<Company> companies = HibernateSupport.readMoreObjects(Company.class, new ArrayList<Criterion>());
		int size = companies.size();
		double[] news_count_values = new double[size];
		for(int i = 0; i < size; i++) {
			System.out.println((i + 1) + "/" + size);
			news_count_values[i] = companies.get(i).getNumberOfNews();
		}
		
		NewsHistogram nh = new NewsHistogram("title", 
				news_count_values, 
				 "description",
			     "Correlation Coefficient",
			     "#News",
			     200);
		
		nh.execute();
	}

	/**
	 * The main method.
	 *
	 * @param argv the arguments
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public static void main(String[] argv) throws SecurityException, IOException {
		TextAnalyserManager tam = new TextAnalyserManager();
		
		Logger logger = Logger.getLogger("MyLogger");
		logger.setUseParentHandlers(false);
		String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	  	FileHandler fh = new FileHandler("data/tmp/addsentencesentiment-"+ date + ".log", true);  
	  	logger.addHandler(fh);
		
		List<News> company_news = new ArrayList<News>();
		News single_company_news;
		HibernateSupport.beginTransaction();
		int news_size = (int) HibernateSupport.getCurrentSession().createCriteria(News.class)
				.setProjection(Projections.rowCount())
				.uniqueResult();
		HibernateSupport.commitTransaction();
		System.out.println(news_size);
		
		int counter = 0;
		int increment_by = 500;
		for(int offset = 0; offset < news_size; offset += increment_by) {
			company_news.clear();
			System.gc();
			company_news = HibernateSupport.readMoreObjects(News.class, new ArrayList<Criterion>(), offset, increment_by);
			System.out.println(offset + " news, of " + news_size + " edited.");

			//HibernateSupport.beginTransaction();

			while(company_news.size() > 0) {
				single_company_news = company_news.get(0);
				company_news.remove(0);
				if(!tam.sensium_analyser.addSentenceSentiment(single_company_news)) {
					logger.info("language: " + single_company_news.getLanguage() + " news-hash: " + single_company_news.getHash());
				}
				System.out.println(++counter + "/" + news_size);
			}
			//HibernateSupport.commitTransaction();

		}
		
		return;
		/*try {
			tam.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}*/

	}

	
}
