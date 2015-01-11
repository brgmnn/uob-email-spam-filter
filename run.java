/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	run.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashSet;

public class run {
	public static final double cross_fold_percent = 0.1;
	public static final int no_training_emails = 2500;

	// whether to use the last division of files for training and test
	public static final boolean use_last_fold = true;
	// whether to use the last serialized data files for the classifier
	public static final boolean use_last_data = false;

	//		file array serialization/deserialization
	// serialize
	public static void serialize_file_array(String path, File[] emails) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			out.writeObject(emails);
			out.close();
			fileOut.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	// deserialize
	public static File[] deserialize_file_array(String path) {
		try {
			FileInputStream file_in = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(file_in);
			
			File[] emails = (File[])in.readObject();
			in.close();
			file_in.close();
			return emails;
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException c) {
			System.out.println("Class not found.");
			c.printStackTrace();
		}
		return null;
	}

	// generate a new cross fold and create all the serialization data files needed.
	public static void generate_new_cross_fold(String data_path) {
		String dir = "train/";
		File[] contents = new File(dir).listFiles();

		if (contents != null) {
			int sum_emails, sum_train, sum_test;
			sum_emails = contents.length;
			sum_test = (int)(sum_emails * run.cross_fold_percent);
			sum_train = sum_emails - sum_test;
			


			//System.out.println("total: "+sum_emails+"\ntest: "+sum_test+"\ntrain: "+sum_train);

			
			int[][] test_indicies = new int[(int)(1/cross_fold_percent)][sum_test];
			Random rnd = new Random();

			LinkedList<Integer> indicies = new LinkedList<Integer>();
			for (int i = 0; i < sum_emails; i++) {
				indicies.add(new Integer(i));
			}

			// generate the random test indicies array
			for (int i = 0; i < test_indicies.length; i++) {
				for (int j = 0; j < sum_test; j++) {
					test_indicies[i][j] = ((Integer)indicies.remove(rnd.nextInt(indicies.size())));
				}
			}

			// check if there are collisions in the data
			HashSet<Integer> collisions = new HashSet<Integer>();
			for (int i = 0; i < test_indicies.length; i++) {
				for (int j = 0; j < test_indicies[i].length; j++) {
					if (!collisions.add(new Integer(test_indicies[i][j]))) {
						System.out.println("   Collision! Number "+test_indicies[i][j]);
					}
				}
			}

			for (int i = 0; i < test_indicies.length; i++)
				Arrays.sort(test_indicies[i]);

			

			//System.out.println("got "+test_indicies.length+" random folds!");

			// loop over each fold of indicies
			for (int i = 0; i < test_indicies.length; i++) {
				File[] training_emails;
				File[] test_emails;

				if (use_last_fold) {
					test_emails = run.deserialize_file_array(data_path+"test-emails-fold-"+i+".ser");
					training_emails = run.deserialize_file_array(data_path+"training-emails-fold-"+i+".ser");
				} else {
					test_emails = new File[sum_test];
					training_emails = new File[sum_train];

					// create the list of test emails
					for (int j = 0; j < test_indicies[i].length; j++) {
						test_emails[j] = contents[test_indicies[i][j]];
					}

					// loop over and add the training emails to the training_emails array
					for (int j = 0, k = 0, m = 0; j < sum_emails; j++) {
						if (test_indicies[i][k] != j) {	
							training_emails[m] = contents[j];
							m++;
							// System.out.println("added "+contents[j].getName());
						} else if (k+1 < test_indicies[i].length) {
							k++;
						}
					}
				}

				// new bayesian classifier for each fold with the two classes
				// BayesianClassifier bayes = new BayesianClassifier();
				// bayes.add_preprocessor_callback(new Preprocessor());
				// bayes.add_class("ham");
				// bayes.add_class("spam");
				ComponentBayesianClassifier cbay = new ComponentBayesianClassifier();
				// cbay.add_preprocessor_callback(new Preprocessor.PassThrough());
				cbay.add_component("subject", new Preprocessor.HeaderSubject());
				cbay.add_component("body", new Preprocessor.Body());
				cbay.add_component("from", new Preprocessor.EmailAddresses());
				// cbay.add_component("subject-2-grams", new Preprocessor.Subject2Grams());
				// cbay.add_component("body-2-grams", new Preprocessor.Body2Grams());
				// cbay.add_component("body-3-grams", new Preprocessor.Body3Grams());
				// cbay.add_component("subject-3-grams", new Preprocessor.Subject3Grams());

				cbay.add_class("ham");
				cbay.add_class("spam");

				cbay.set_component_weighting("from", 0.6);
				cbay.set_component_weighting("subject", 1.5);
				cbay.set_component_weighting("body", 2.75);
				// cbay.set_component_weighting("subject-2-grams", 1.5);
				// cbay.set_component_weighting("body-2-grams", 2.75);

				// System.out.println("  Training classifier");
				// loop over and train the bayesian classifier
				for (int j = 0; j < training_emails.length; j++) {
					String str_class = contents[j].getName().startsWith("ham") ? "ham" : "spam";
					cbay.add_training_email(contents[j], str_class);
				}

				// System.out.println("  Calculating data");
				cbay.calculate();
				// System.out.println("  Saving hash tables for fold "+(i+1));
				cbay.save_data(data_path+"data-fold-"+i+".ser");
				// System.out.println("  Saving file lists");

				if (!use_last_fold) {
					run.serialize_file_array(data_path+"training-emails-fold-"+i+".ser", training_emails);
					run.serialize_file_array(data_path+"test-emails-fold-"+i+".ser", test_emails);
				}
				// System.out.println("  Finished fold"+(i+1));
			}

		}
	}

	// test the bayesian classifier
	public static double[] validate_cross_fold(String data_path, double subject_weight, double body_weight) {
		int folds = (int)(1/cross_fold_percent);
		int all_right = 0;
		int all_emails = 0;

		double[] perc_correct = new double[folds];

		for (int i = 0; i < folds; i++) {
			ComponentBayesianClassifier cbay = new ComponentBayesianClassifier();
			// System.out.println("  Reading in hash table for fold "+(i+1));
			cbay.load_data(data_path+"data-fold-"+i+".ser");

			// System.out.println("  Reading file array");
			File[] emails = run.deserialize_file_array(data_path+"test-emails-fold-"+i+".ser");
			//bayes.calculate();

			int right = 0;

			//System.out.println("Fold "+i+":");
			System.out.println("  Miss-classified emails:");

			for (int j = 0; j < emails.length; j++) {
				String str_class = emails[j].getName().startsWith("ham") ? "ham" : "spam";
				String bayes_class = cbay.classify(emails[j]);

				//System.out.println(emails[j].getName()+"	is: "+str_class+"	=> "+bayes_class);

				if (str_class.equals(bayes_class))
					right++;
				else {
					System.out.println("    "+emails[j]);
				}
			}

			perc_correct[i] = (double)right*100/emails.length;
			//System.out.println("  Correctly classified emails = "+perc_correct[i]+"%");
			
			all_emails += emails.length;
			all_right += right;
		}

		// System.out.println("\nAverage = "+Statistics.mean(perc_correct)+"%");
		// System.out.print("  Standard Deviation = ");
		// System.out.printf("%.3f", Statistics.stdev(perc_correct));
		// System.out.println("%");

		return perc_correct;
	}

	public static void optimise_weightings(double lower[], double upper[], double step[]) {
		//int d1_size = (int)((upper - lower)/step);
		//double[][] percs = new double[d1_size][d1_size];
		double[] percs;

		//System.out.println(d1_size);

		for (double j = lower[1]; j < upper[1]; j+= step[1]) {
			for (double i = lower[0]; i < upper[0]; i+=step[0]) {
				percs = run.validate_cross_fold("cross-fold/1/",i,j);

				System.out.println("subject = "+i+" body = "+j);
				System.out.print("    Average = ");
				System.out.printf("%.5f", Statistics.mean(percs));
				System.out.print("% +- ");
				System.out.printf("%.5f", Statistics.stdev(percs));
				System.out.println("%");
			}
		}
	}

	public static void main( String[] args ) {
		int repeats = 1;

		//System.out.println(args.length+" "+args[0]);

		if (args.length > 0) {
			try {
				repeats = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("Bad command line argument!");
			}
		}

		//run.optimise_weightings(new double[]{1.25, 2.75}, new double[]{1.75, 3.25}, new double[]{0.125, 0.125});

		double[] correct = new double[repeats*(int)(1/cross_fold_percent)];

		System.out.println("Testing with "+repeats+" iterations.");
		System.out.println("    "+repeats*no_training_emails+" emails will be classified.");
		System.out.println("    "+(int)(repeats*(no_training_emails-no_training_emails*cross_fold_percent)*(int)(1/cross_fold_percent))+" emails will be used for training.\n");

		for (int i = 0; i < repeats; i++) {
			System.out.print("  Processing iteration "+(i+1)+"...");

			if (!use_last_data) run.generate_new_cross_fold("cross-fold/"+(i+1)+"/");
			
			double[] perc_fold = run.validate_cross_fold("cross-fold/"+(i+1)+"/", 1.5, 2.75);

			for (int j = 0; j < (int)(1/cross_fold_percent); j++) {
				correct[i*(int)(1/cross_fold_percent)+j] = perc_fold[j];
			}

			System.out.println(" Complete. Average = "+Statistics.mean(perc_fold));

			for (int j = 0; j < perc_fold.length; j++) {
				System.out.println(perc_fold[j]);
			}
		}

		System.out.print("\nAverage = ");
		System.out.printf("%.3f", Statistics.mean(correct));
		System.out.print("%\n  Standard Deviation = ");
		System.out.printf("%.3f", Statistics.stdev(correct));
		System.out.println("%");
	}
}