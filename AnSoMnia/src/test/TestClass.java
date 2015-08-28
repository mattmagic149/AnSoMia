package test;

import general.MainApplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mining.CompanyIndexIndustryCrawler;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import analysers.MarketValueAnalyser;
import utils.HibernateSupport;
import utils.HttpRequestManager;
import utils.HttpRequester;
import database.Company;
import database.MarketValue;

public class TestClass {
	
	public static HttpRequestManager http_req_manager = new HttpRequestManager();
	public static TestClass t_class;
	public static String wall_street_url = "http://www.wallstreet-online.de";	


	public static void main(String[] args) {
		
		Company company_1 = HibernateSupport.readOneObjectByStringId(Company.class, "AT000000STR1");
		Company company_2 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000AMAG3");
		Company company_3 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000BENE6");
		Company company_4 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000FACC2");
		
		

		DateFormat date_format = new SimpleDateFormat("dd.MM.yy");
		Date from;
		Date to;
		try {
			from = date_format.parse("02.08.15");
			to = date_format.parse("20.08.15");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		ArrayList<MarketValue> values_1 = company_3.getMarketValuesBetweenDates(from, to);
		ArrayList<MarketValue> values_2 = company_4.getMarketValuesBetweenDates(from, to);
		
		MarketValueAnalyser mva = new MarketValueAnalyser();
		mva.normalizeArrays(values_1, values_2);
		
		System.out.println(mva.calculateCorrelationCoefficient(values_1, values_2));
	}
	


}
