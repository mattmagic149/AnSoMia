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
		
		NewsDetail details = new NewsDetail(news, 
															"sensium", 
															"en", 
															resp.polarity.score, 
															resp.objectivity.score);

		HibernateSupport.beginTransaction();
		
		news.addCompanyNewsDetails(details);
		SentenceInformation info;
		for(Sentence sentence: resp.sentences) {
			info = new SentenceInformation(details, sentence.start, sentence.end, Float.MIN_VALUE);
			details.addSentenceInformation(info);
		}
		
		HibernateSupport.commitTransaction();
		
		return true;
	}

	
}
