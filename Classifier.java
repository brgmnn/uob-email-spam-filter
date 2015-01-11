/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	Classifier.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.io.*;

abstract class Classifier implements java.io.Serializable {
	protected ProcessEmailCallback preprocessor = null;

	// all classifiers must have a preprocessor callback function assignment method. this must be
	// called with a valid preprocessor before the classifier can be used.
	public void add_preprocessor_callback(ProcessEmailCallback preprocessor) {
		if (this.preprocessor == null)
			this.preprocessor = preprocessor;
	}

	abstract void add_class(String name);
	abstract void add_training_email(String email, String str_class);
	public void add_training_email(File email, String str_class) {
		this.add_training_email(this.read_email_file(email), str_class);
	}

	// classify functions. they must implement the classify(string) function however we will provide
	// the classify(file) function as it should be the same across classes. we enforce now that it
	// must be the same. ie classify(string) should have the preprocessing done inside it
	abstract String classify(String email);
	public String classify(File email) {
		return this.classify(this.read_email_file(email));
	}

	abstract void load_data(String path);
	abstract void save_data(String path);

	abstract void calculate();

	abstract double get_class_prior(String str_class);
	//abstract double get_p_word_given_class(String str_word, String str_class);
	abstract double get_p_class_given_email(String str_class, String str_email);

	// read an email file in to a raw string. we can define this here as it is the same across all
	// classifier classes.
	public String read_email_file(File email) {
		try {
			StringBuffer file_data = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(
									new FileInputStream(email)));
			char[] buffer = new char[1024];
			int num_read=0;

			while((num_read=br.read(buffer)) != -1) {
				String read_data = String.valueOf(buffer, 0, num_read);
				file_data.append(read_data);
			}
			br.close();

			return file_data.toString();
		} catch (IOException e) {
			System.out.println("problem bro?");
		}
		return "";
	}
}