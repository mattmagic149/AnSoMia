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
package database;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import interfaces.*;
import io.sensium.NamedEntity;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.*;

/**
 * The Class CompanyInformation.
 */
@Entity
public class CompanyInformation implements ISaveAndDelete {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long ci_id;
	
	private String name;
	
	private String dbpedia_link;
	
	private String company_homepage;
	
	@Column(length = 8000)
	private String dbpedia_content;
	
	@OneToMany
	@JoinColumn(name="ci_id")
	private List<Company> companies;
	
	private Date date_added;
	
	public CompanyInformation() {
		this.companies = new ArrayList<Company>();
	}
	
	public CompanyInformation(String name, String dbpedia_link, String company_homepage,
								String dbpedia_content, Date date_added) {
		this.name = name;
		this.dbpedia_link = dbpedia_link;
		this.company_homepage = company_homepage;
		this.dbpedia_content = dbpedia_content;
		this.date_added = date_added;
		this.companies = new ArrayList<Company>();
	}
	
	public long getId() {
		return ci_id;
	}

	public String getName() {
		return name;
	}

	public String getDbpediaLink() {
		return dbpedia_link;
	}

	public String getCompanyHomepage() {
		return company_homepage;
	}

	public String getDbpediaContent() {
		return dbpedia_content;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public Date getDateAdded() {
		return date_added;
	}

	public boolean addCompany(Company company) {
		boolean success = false;
		//company.setInfo(this);
		if(!this.companies.contains(company)) {
			if (this.companies.add(company)){
				company.setInfo(this);
				success = company.saveToDB();
			}
		}
		return success;
	}
	
	public static CompanyInformation getCorrespondingCompanyInformation(NamedEntity entity,
												HttpRequestManager http_manager) {
		CompanyInformation c_info;
		List<Criterion> cr = new ArrayList<Criterion>();
		cr.add(Restrictions.eq("name", entity.normalized));
		c_info = HibernateSupport.readOneObject(CompanyInformation.class, cr);
		
		if(c_info == null) {
			System.out.println("c_info == NULL");
			HttpRequester requester = http_manager.getCorrespondingHttpRequester(entity.link.replace("%26", "&"));
			Element response = requester.getHtmlContentWithCompleteUrl(entity.link.replace("%26", "&"));
			if(response == null) {
				return null;
			}
			String homepage = extractHompage(response);
			if(homepage == null) {
				System.out.println("homepage does not fullfill its requirements");
				return null;
			} else if(homepage.length() < 2) {
				System.out.println("homepage does not fullfill its requirements");
				return null;
			}
			c_info = new CompanyInformation(entity.normalized, entity.link.replace("%26", "&"), homepage,
					response.outerHtml(), new Date());
			
			HibernateSupport.beginTransaction();
			c_info.saveToDB();			
			c_info.mapInfoToCompany(homepage);
			HibernateSupport.commitTransaction();

		}
		
		return c_info;
	}
	
	@SuppressWarnings("unchecked")
	private void mapInfoToCompany(String homepage) {
		if(homepage != null && homepage.length() > 2) {
			System.out.println(homepage);
			String[] tmp = homepage.split("\\.");
			if(tmp.length < 2) {
				return;
			}
			Criteria c = HibernateSupport.getCurrentSession().createCriteria(Company.class);
			c.add(Restrictions.like("web_site", "%" + "www." + homepage.split("\\.")[1] + "%"));

			List<Company> companies = c.list();
			System.out.println(companies.size());
			for(int i = 0; i < companies.size(); i++) {
				this.addCompany(companies.get(i));
			}
		}
		
	}
	
	private static String extractHompage(Element response) {
		String result = null;
		String[] tmp;
		if(response != null) {
			Elements ankers = response.select(".description tbody a");
			for(int j = 0; j < ankers.size(); j++) {
				if(ankers.get(j).text().contains("homepage")) {
					System.out.println(ankers.get(j).parent().parent());

					System.out.println(ankers.get(j).parent().parent().children().last());
					result = ankers.get(j).parent().parent().children().last().select("li").text();
					
					try {
					    result = new URI(result).getHost();
					} catch (URISyntaxException e) {
					    // Certainly not an URL
						continue;
					}
					
					/*System.out.println(result);
					tmp = result.split("/");
					//result = tmp[tmp.length - 1];
					for(int k = 0; k < tmp.length; k++) {
						if(tmp[k].contains("www.")) {							
							
							result = tmp[k];
						}
					}*/
				}
			}
		}
		System.out.println(result);
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#saveToDB()
	 */
	@Override
	public boolean saveToDB() {
		if(!HibernateSupport.commit(this))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#deleteFromDB(java.lang.Object)
	 */
	@Override
	public void deleteFromDB(Object obj) {
		HibernateSupport.deleteObject(this);	
	}

	@Override
	public String serialize() {
		// TODO Auto-generated method stub
		return null;
	}

}
