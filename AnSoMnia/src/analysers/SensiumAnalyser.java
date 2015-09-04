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

import utils.HibernateSupport;
import database.News;
import database.NewsDetail;
import database.SentenceInformation;
import io.sensium.ExtractionRequest;
import io.sensium.ExtractionRequest.Extractor;
import io.sensium.ExtractionResponse;
import io.sensium.Sensium;
import io.sensium.SensiumException;
import io.sensium.Sentence;
import io.sensium.SentimentOccurrence;

// TODO: Auto-generated Javadoc
/**
 * The Class SensiumAnalyser.
 */
public class SensiumAnalyser
{
	
	/** The sensium. */
	private Sensium sensium;
	
	/** The req. */
	private ExtractionRequest req;
	
	/** The resp. */
	private ExtractionResponse resp;
	
	/**
	 * Instantiates a new sensium analyser.
	 */
	public SensiumAnalyser() {
		System.out.println("SensiumAnalyser ctor called");
		this.sensium = new Sensium("e16c27a8-e309-47aa-838d-cc2e6ffc5007");
		this.req = new ExtractionRequest();
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
											resp.objectivity.score);

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

	
}
