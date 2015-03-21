package org.ist.OAD13.Support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import oadTurk.User.Admin;
import oadTurk.User.Creator;
import oadTurk.User.RootUser;
import oadTurk.User.User;
import oadTurk.Structure.*;
import oadTurk.Exam.Exam;
import oadTurk.Exam.Participate_Exam;
import oadTurk.Exam.PreferedCategory;
import oadTurk.LearningUnit.*;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;



/**
 * This Class handles everything for communicating with the database
 * 
 * @author Stettinger
 *
 */

public class HibernateSupport {
	
	private static SessionFactory sessionFactory;

	static {
		System.out.println("HibernateSupport: Constructor");
		init();
	}
	
	public static void create(){
		// function is not necessary it only activates the static construction above
	}
	
	private static void init() {
		//Change the path to your deployed config file !
		File configFile = new File("/Users/matthiasivantsits/Documents/workspace/oadTurk/build/classes/hibernate.cfg.xml");

		Configuration configuration = new Configuration();
		
		//add all classes you want to annotate
		configuration.addAnnotatedClass(RootUser.class);
		configuration.addAnnotatedClass(User.class);
		configuration.addAnnotatedClass(Creator.class);
		configuration.addAnnotatedClass(Admin.class);
		configuration.addAnnotatedClass(LearningEnvironment.class);	
		configuration.addAnnotatedClass(LearningApplication.class);	
		configuration.addAnnotatedClass(Category.class);	
		configuration.addAnnotatedClass(LearningUnit.class);	
		configuration.addAnnotatedClass(Answer.class);	
		configuration.addAnnotatedClass(Evaluate.class);	
		configuration.addAnnotatedClass(Exam.class);
		configuration.addAnnotatedClass(PreferedCategory.class);
		configuration.addAnnotatedClass(Participate_Exam.class);


		
		configuration.configure(configFile);
		
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	public static Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	
	public static void beginTransaction() {
		getCurrentSession().beginTransaction();
	}
	
	public static void commitTransaction() {
		getCurrentSession().getTransaction().commit();
	}
	
	public static boolean commit(Object obj) {
		try {
			getCurrentSession().saveOrUpdate(obj);
		}
		catch (HibernateException e) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T> List <T> readMoreObjects(Class<?> classToRetrieve, List<Criterion> criterions) {
		beginTransaction();
		Criteria criteria = getCurrentSession().createCriteria(classToRetrieve);
		for(Criterion criterion: criterions) {
			criteria.add(criterion);
		}
		List<T> result = criteria.list();
		commitTransaction();
		return result;
	}
	
	public static <T> T readOneObject(Class<?> classToRetrieve, List<Criterion> criterions) {
		List<T> result = readMoreObjects(classToRetrieve, criterions);
		return (result.size() > 0) ? (result.get(0)):(null);
	}
	
	public static <T> T readOneObjectByID(Class<?> classToRetrieve, int id) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.idEq(id));
		T result = readOneObject(classToRetrieve, criterions);
		return result;
	}
	
	public static <T> void deleteObject(T objectToDelete) {
		getCurrentSession().delete(objectToDelete);
	}

}
