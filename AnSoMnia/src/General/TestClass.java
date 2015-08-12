package General;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;

import Support.HibernateSupport;

public class TestClass {

	public static void main(String[] args) {
		List<CompanyNews> all_news = HibernateSupport.readMoreObjects(CompanyNews.class, new ArrayList<Criterion>());
		CompanyNews news;
		int bigger_size_count = 0;
		int bigger_length_count = 0;

		for(int i = 0; i < all_news.size(); i++) {
			news = all_news.get(i);
			if(news.getCompanies().size() > 1) {
				//System.out.println("the company.size() of this news is: " + news.getCompanies().size());
				//System.out.println("text = " + news.getContent());
				bigger_size_count++;
			}
			if(news.getContent().length() < 100) {
				System.out.println("text = " + news.getContent());
				System.out.println("url = " + news.getUrl());
				bigger_length_count++;
			}
		}
		
		System.out.println("bigger_size_count = " + bigger_size_count);
		System.out.println("bigger_size_count = " + bigger_length_count);

	}

}
