/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	train.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;

public class train
	{
		public static void scan_training_dir( Classifier classifier ) {
			String dir = "train/";

			File[] contents = new File(dir).listFiles();
			String email_name;

			if (contents != null) {
				for ( File email_file : contents ) {
					email_name = email_file.getName();

					String str_class = email_name.startsWith("ham") ? "ham" : "spam";
					classifier.add_training_email( email_file, str_class );
				}
			}
		}

		public static void main( String[] args ) {
			ComponentBayesianClassifier bayes = new ComponentBayesianClassifier();
			//bayes.save_data("blank.ser");
			bayes.add_component("subject", new Preprocessor.HeaderSubject());
			bayes.add_component("body", new Preprocessor.Body());
			bayes.add_component("subject-2-grams", new Preprocessor.Subject2Grams());
			bayes.add_component("body-2-grams", new Preprocessor.Body2Grams());

			bayes.add_class("ham");
			bayes.add_class("spam");

			bayes.set_component_weighting("subject", 1.5);
			bayes.set_component_weighting("body", 2.75);
			bayes.set_component_weighting("subject-2-grams", 1.5);
			bayes.set_component_weighting("body-2-grams", 2.75);

			train.scan_training_dir(bayes);

			bayes.calculate();
			bayes.save_data("classifier-data.ser");
			// // // bayes.load_data("bayesian-classifier-data.ser");

			System.out.println(bayes);

			// System.out.println("email:"+args[0]);
			// System.out.println("result: "+bayes.classify(args[0]));

			// // bayes.pf.end();
			// //System.err.println(bayes.pf.toString());

			// BayesianClassifier bayes = new BayesianClassifier();
			// bayes.add_preprocessor_callback(new Preprocessor.EmailAddresses());
			// bayes.add_class("ham");
			// bayes.add_class("spam");

			// train.scan_training_dir(bayes);

			// ArrayList<Map.Entry> email_addrs = new ArrayList<Map.Entry>(bayes.classes.get("spam").words.entrySet());

			// for (int i = 0; i < email_addrs.size(); i++) {
			// 	System.out.println(((Word)email_addrs.get(i).getValue())+"	= "+((String)email_addrs.get(i).getKey()));
			// }
		}
	}
