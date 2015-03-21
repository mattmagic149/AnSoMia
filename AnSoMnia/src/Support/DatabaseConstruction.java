package Support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2ddl.SchemaExport;



/**
 * This Class is for Constructing the Database
 * 
 * @author Stettinger
 *
 */

public class DatabaseConstruction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Creating database-structure for oadTurk");
		
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
		
		configuration.configure("hibernate.cfg.xml");

		new SchemaExport(configuration).create(true, true);
		

		// Add one User from each permission
		
		// User
		System.out.println("adding one standard-user for each permission: User");
		List<Criterion>  criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("firstname", "user"));
		criterions.add(Restrictions.eq("surname", "user"));
		User user = HibernateSupport.readOneObject(User.class, criterions);

		if(user == null){
			user = new User("user","user","user@gmail.net","0123456");
			HibernateSupport.beginTransaction();
			user.saveToDB();
			HibernateSupport.commitTransaction();
		}
		
		/*
		// Creator
		System.out.println("...Creator");
		criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("firstname", "creator"));
		criterions.add(Restrictions.eq("surname", "creator"));
		Creator creator = HibernateSupport.readOneObject(User.class, criterions);

		if(creator == null){
			creator = new Creator("creator","creator","creator@gmail.net","0123457");
			HibernateSupport.beginTransaction();
			creator.saveToDB();
			HibernateSupport.commitTransaction();
		}
		// Admin
		System.out.println("...and Admin.");
		criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("firstname", "admin"));
		criterions.add(Restrictions.eq("surname", "admin"));
		Admin admin = HibernateSupport.readOneObject(User.class, criterions);

		if(admin == null){
			admin = new Admin("admin","admin","user@gmail.net");
			HibernateSupport.beginTransaction();
			admin.saveToDB();
			HibernateSupport.commitTransaction();
		}
		*/
		
		// create user 
		user = (User) RootUser.register("Heinz", "Fischer", "fischi@gv.at", "012345", "fischi");
		
		if (user!=null){
			Admin admin = new Admin(user.getFirstname(), user.getSurname(),user.getEmail());
			admin.setPasswort_hash(user.getPasswort_hash());
			admin.setSalt(user.getSalt());
			admin.setLast_password_change(new Date());
			admin.setStatus_flag(UserState.ACTIVE);
			
			HibernateSupport.beginTransaction();
			admin.saveToDB();
			user.deleteFromDB();
			HibernateSupport.commitTransaction();
		}
		
		// Adding LEarning Environment: TUG
		
		System.out.println("adding Learning Environment: TUG");
		
		criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("university", "TUG"));
		LearningEnvironment l_environment = HibernateSupport.readOneObject(LearningEnvironment.class, criterions);
		
		if(l_environment == null){
			l_environment = new LearningEnvironment("TUG");
			HibernateSupport.beginTransaction();
			l_environment.saveToDB();
			HibernateSupport.commitTransaction();
		}
		
		// Adding Learning Application: OAD
		
		System.out.println("adding Learning Application: OAD");
		
		criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("name", "OAD"));
		LearningApplication l_app = HibernateSupport.readOneObject(LearningApplication.class, criterions);

		if(l_app == null){
			l_app = new LearningApplication("OAD");
			l_environment.addLearningApplication(l_app);
			HibernateSupport.beginTransaction();
			l_environment.saveToDB();
			HibernateSupport.commitTransaction();
		}
		
		
		// Adding Category: Einf�hrung
		
		System.out.println("adding Category: Einf�hrung");
		
		criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("name", "Einf�hrung"));
		Category l_category = HibernateSupport.readOneObject(LearningApplication.class, criterions);

		if(l_category == null){
			l_category = new Category("Einf�hrung");
			l_app.addCategory(l_category);
			HibernateSupport.beginTransaction();
			l_app.saveToDB();
			HibernateSupport.commitTransaction();
		}
		
		// Adding LU: Wieviele Tage?
	
		System.out.println("adding LU: Wieviele Tage?");
		
		criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("text", "1 Wieviele Tage?"));
		LearningUnit l_unit = HibernateSupport.readOneObject(LearningUnit.class, criterions);

		if(l_unit == null){
			l_unit = new LearningUnit("1 Wieviele Tage?");
			l_category.addLearningUnit(l_unit);
		}
		
		// Adding LU: 
		
		System.out.println("adding LU: Was ist?");
		
		criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("text", "2 Was ist?"));
		l_unit = HibernateSupport.readOneObject(LearningUnit.class, criterions);

		if(l_unit == null){
			l_unit = new LearningUnit("2 Was ist?");
			l_category.addLearningUnit(l_unit);
		}
		
		// Adding Answer
		
		System.out.println("adding answer");
		
		criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("text", "sieben"));
		Answer answer = HibernateSupport.readOneObject(Answer.class, criterions);

		if(answer == null){
			answer = new Answer("sieben");
			l_unit.addAnswer(answer);
			HibernateSupport.beginTransaction();
			l_unit.saveToDB();
			HibernateSupport.commitTransaction();
		}
		



		System.out.println("Finished");
	}

}
