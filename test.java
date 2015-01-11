/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	filter.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.io.*;
import java.util.HashMap;

public class test {
	public static class MySubjectProcessor implements ProcessEmailCallback {
		public String process_email(String email) {
			email = (email.split(";",2))[0];
			return (new Preprocessor()).process_email(email);
		}
	}

	public static class MyBodyProcessor implements ProcessEmailCallback {
		public String process_email(String email) {
			email = (email.split(";",2))[1];
			return (new Preprocessor()).process_email(email);
		}
	}

	public static void main( String[] args ) {
		// BayesianClassifier bayes = new BayesianClassifier();

		// bayes.add_class("ham");
		// bayes.add_class("spam");
		// bayes.add_preprocessor_callback(new Preprocessor());

		// bayes.add_training_email("Hi daniel, it's mum here. How are you?", "ham");
		// bayes.add_training_email("FREE COCK IN THE PUSSY!", "spam");
		// bayes.add_training_email("Steam, your purchase was successful.", "ham");

		// bayes.calculate();

		// System.out.println(bayes);

		// System.out.println("  ham given COCK : "+bayes.get_p_class_given_email("ham", "COCK daniel Steam"));
		// System.out.println("  spam given COCK: "+bayes.get_p_class_given_email("spam", "COCK daniel Steam"));
		// System.out.println(bayes.classify("COCK daniel Steam"));
		
/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Component Bayesian Classifier time
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/

 		HashMap<String, Word> email_addr = new HashMap<String, Word>();
		System.out.println();

		// ComponentBayesianClassifier cbay = new ComponentBayesianClassifier();
		// cbay.add_component("subject", new MySubjectProcessor());
		// cbay.add_component("body", new MyBodyProcessor());

		// cbay.add_class("ham");
		// cbay.add_class("spam");

		// System.out.println((new Preprocessor.HeaderSubject()).process_email(cbay.read_email_file(new File("train/ham1.txt"))));

		// cbay.add_training_email("it's mum what are you doing for easter; Hi daniel, it's mum here. How are you?", "ham");
		// cbay.add_training_email("completed purchase; Steam, your purchase was successful.", "ham");
		// cbay.add_training_email("FREE COCK NOW; FREE COCK IN THE PUSSY!", "spam");

		// cbay.calculate();

		// System.out.println(cbay);

		// System.out.println("  ham given COCK : "+cbay.get_p_class_given_email("ham", "COCK FOR FREE; COCK daniel Steam"));
		// System.out.println("  spam given COCK: "+cbay.get_p_class_given_email("spam", "COCK FOR FREE; COCK daniel Steam"));
		// System.out.println(cbay.classify("COCK FOR FREE; COCK daniel Steam"));
	}
}
