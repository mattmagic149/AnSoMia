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
import java.util.Date;
import java.util.List;

import utils.HibernateSupport;
import utils.HttpRequestManager;
import database.Company;
import database.CompanyInformation;
import database.EntityInformation;
import database.News;
import database.NewsDetail;
import database.SentenceInformation;
import io.sensium.ExtractionRequest;
import io.sensium.ExtractionRequest.Extractor;
import io.sensium.ExtractionResponse;
import io.sensium.NamedEntity;
import io.sensium.Occurrence;
import io.sensium.Sensium;
import io.sensium.SensiumException;
import io.sensium.Sentence;
import io.sensium.SentimentOccurrence;

// TODO: Auto-generated Javadoc
/**
 * The Class SensiumAnalyser.
 * @param <Occurence>
 */
public class SensiumAnalyser<Occurence>
{
	
	/** The sensium. */
	private Sensium sensium;
	
	/** The req. */
	private ExtractionRequest req;
	
	/** The resp. */
	private ExtractionResponse resp;
	
	/** The date_added. */
	private Date date_added;
	
	private HttpRequestManager http_manager;
	
	/**
	 * Instantiates a new sensium analyser.
	 */
	public SensiumAnalyser() {
		System.out.println("SensiumAnalyser ctor called");
		this.sensium = new Sensium("e16c27a8-e309-47aa-838d-cc2e6ffc5007");
		this.req = new ExtractionRequest();
		this.date_added = new Date();
		this.http_manager = HttpRequestManager.getInstance();
	}
	
	/**
	 * Analyse text.
	 *
	 * @param news the news
	 * @return true, if successful
	 */
	public boolean analyseText(News news) {
		
		if(news.getLanguage().equals("en")) {
			this.req.text = news.getContent();
		} else if(news.getLanguage().equals("de")) {
			this.req.text = news.getTranslatedContent();
		} else {
			return false;
		}
		
		req.extractors = new Extractor[] { Extractor.Sentences, 
										   Extractor.Sentiment
										  };
		
		try {
			this.resp = this.sensium.extract(req);
		} catch (SensiumException e) {
			e.printStackTrace();
			return false;
		}
		
		NewsDetail detail = new NewsDetail(news, 
											"sensium", 
											"en", 
											resp.polarity.score, 
											resp.objectivity.score,
											this.date_added);

		HibernateSupport.beginTransaction();
		
		news.addCompanyNewsDetails(detail);
		
		ArrayList<SentenceInformation> infos = new ArrayList<SentenceInformation>();
		SentenceInformation info;
		List<SentimentOccurrence> polarity_occ = resp.polarity.occurrences;
		List<SentimentOccurrence> objectivity_occ = resp.objectivity.occurrences;
		List<Sentence> sentences = resp.sentences;
		
		for(int i = 0; i < sentences.size(); i++) {
			info = new SentenceInformation(detail, 
									sentences.get(i).start, 
									sentences.get(i).end, 
									Double.MAX_VALUE, 
									Double.MAX_VALUE);
			
			infos.add(info);
		}
		
		for(int i = 0; i < polarity_occ.size(); i++) {
			infos.get(i).setPolarity(polarity_occ.get(i).score);;
		}
		
		for(int i = 0; i < objectivity_occ.size(); i++) {
			infos.get(i).setObjectivity(objectivity_occ.get(i).score);;
		}
		
		for(int i = 0; i < infos.size(); i++) {
			infos.get(i).saveToDB();
		}
		
		detail.setSentenceInformation(infos);	
		
		HibernateSupport.commitTransaction();
		
		return true;
	}
	
	/**
	 * Adds the sentence sentiment.
	 *
	 * @param news the news
	 * @return true, if successful
	 */
	public boolean addSentenceSentiment(News news) {
		
		if(news.getLanguage().equals("en")) {
			this.req.text = news.getContent();
		} else if(news.getLanguage().equals("de")) {
			this.req.text = news.getTranslatedContent();
		} else {
			return false;
		}
		
		req.extractors = new Extractor[] { Extractor.Sentences, 
										   Extractor.Sentiment
										  };
		
		try {
			this.resp = this.sensium.extract(req);
		} catch (SensiumException e) {
			e.printStackTrace();
			return false;
		}
		
		NewsDetail detail = news.getNewsDetails().get(0);
 		ArrayList<SentenceInformation> infos = new ArrayList<SentenceInformation>();
 		SentenceInformation info;
 		
		List<SentimentOccurrence> polarity_occ = resp.polarity.occurrences;
		List<SentimentOccurrence> objectivity_occ = resp.objectivity.occurrences;
		List<Sentence> sentences = resp.sentences;
		
		HibernateSupport.beginTransaction();
		detail.removeAllSentenceInformation();

		for(int i = 0; i < sentences.size(); i++) {
			info = new SentenceInformation(detail, 
									sentences.get(i).start, 
									sentences.get(i).end, 
									Double.MAX_VALUE, 
									Double.MAX_VALUE);
			
			infos.add(info);
		}
		
		for(int i = 0; i < polarity_occ.size(); i++) {
			infos.get(i).setPolarity(polarity_occ.get(i).score);;
		}
		
		for(int i = 0; i < objectivity_occ.size(); i++) {
			infos.get(i).setObjectivity(objectivity_occ.get(i).score);;
		}
		
		for(int i = 0; i < infos.size(); i++) {
			infos.get(i).saveToDB();
		}
		
		detail.setSentenceInformation(infos);		
		HibernateSupport.commitTransaction();
		
		return true;
	}
	
	public boolean getEntitiesAndMapNewsNew(News news) {
		
		if(news.getNewsDetails().size() == 0) {
			System.out.println("I am returning. NewsDetails are not available");
			return false;
		}
		
		if(news.getNewsDetails().get(0).getEntityInformation().size() > 0) {
			System.out.println("I am returning. Already analysed.");
			return false;
		}
		
		if(news.getLanguage().equals("en")) {
			this.req.text = news.getContent();
		} else if(news.getLanguage().equals("de")) {
			this.req.text = news.getTranslatedContent();
		} else {
			return false;
		}
		
		req.extractors = new Extractor[] { Extractor.Entities };
		
		try {
			this.resp = this.sensium.extract(req);
		} catch (SensiumException e) {
			e.printStackTrace();
			return false;
		}
		

		NamedEntity entity;
		EntityInformation e_info;
		CompanyInformation c_info;
		List<Company> companies;
		List<Occurrence> occurrences;
		Occurrence occurence;
		
		NewsDetail detail = news.getCorrespondingNewsDetail("sensium");
		if(detail == null) {
			return false;
		}
		
		for(int i = 0; i < resp.entities.size(); i++) {
			entity = resp.entities.get(i);
			occurrences = entity.occurrences;
			
			if(entity.type.equals("Organisation")) { //Organisation, Location, Person
				c_info = CompanyInformation.getCorrespondingCompanyInformation(entity, this.http_manager);
				
				if(c_info == null) {
					System.out.println("c_info does not fullfill its requirements");
					return false;
				}
				
				//map news.
				HibernateSupport.beginTransaction();
				companies = c_info.getCompanies();
				System.out.println("companies.size() = " + companies.size());

				for(int j = 0; j < companies.size(); j++) {
					companies.get(j).addNewsMapping2(news);
					companies.get(j).saveToDB();
				}				
				
				if(detail.getEntityInformation().size() == 0) {
					for(int j = 0; j < occurrences.size(); j++) {
						occurence = occurrences.get(j);
						e_info = new EntityInformation(detail, 
													   c_info, 
													   occurence.start, 
													   occurence.end);
						
						e_info.saveToDB();	
					}
				}
				HibernateSupport.commitTransaction();
					
			}
		}
		
		System.out.println("Everything worked as expected.");
		return true;
	}

	
}
