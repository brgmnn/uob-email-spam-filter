/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	BayesianClassifier.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.lang.Integer;
import java.lang.Math;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class BayesianClassifier extends Classifier implements java.io.Serializable {
	protected HashMap<String, Class> classes = new HashMap<String, Class>();
	private HashSet<String> words = new HashSet<String>();

	public BayesianClassifier() {}

	public String toString() {
		String info = "Classes:";
		for (Map.Entry<String, Class> class_entry : classes.entrySet()) {
			Class cls = (Class)class_entry.getValue();
			String str_class = (String)class_entry.getKey();

			info += "  "+str_class+":\n";
			info += "    words = "+cls.words.size()+"\n";
			info += "    prior = "+cls.probability+"\n";
			info += "    train = "+cls.training_count+"\n";
			info += "    total words = "+cls.dupe_word_count+"\n";
		}
		return info;
	}

	// ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
	//		add class
	// adds a class to the class table
	public void add_class(String name) {
		classes.put(name, new Class());
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Get probabilities
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public double get_class_prior(String str_class) {
		return Math.log(classes.get(str_class).probability);
	}

	public double get_p_word_given_class(String str_word, String str_class) {
		return classes.get(str_class).words.get(str_word).probability;
	}

	public double get_p_class_given_email(String str_class, String str_email) {
		String[] email_words = preprocessor.process_email(str_email).split(" ");
		Class cls = classes.get(str_class);
		double probability = Math.log( cls.probability );
		// double probability = 0.0;

		// the summation
		for (String str_word : email_words)
			if (str_word.length() > 0 && cls.words.get(str_word) != null)
				probability += Math.log( cls.words.get(str_word).probability );

		return probability;
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Classify
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public String classify(String str_email) {
		String the_class = "";
		double max_prob = 0.0;
		double prob = 0.0;

		for (String str_class : classes.keySet()) {
			prob = get_p_class_given_email(str_class, str_email);

			if (prob > max_prob || the_class.length() == 0) {
				max_prob = prob;
				the_class = str_class;
			}
		}

		return the_class;
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Adding training emails
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public void add_training_email(String str_email, String str_class) {
		if (classes.containsKey(str_class)) {
			Class c_class = classes.get(str_class);
			String[] email_words = preprocessor.process_email(str_email).split(" ");

			for (String str_word : email_words) {
				if (str_word.length() > 0) {
					Word word = c_class.words.get(str_word);
					this.words.add(str_word);
					c_class.dupe_word_count++;

					if (word != null) {
						word.count++;
					} else {
						c_class.words.put(str_word, new Word());
					}

					Iterator it = this.classes.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pairs = (Map.Entry)it.next();

						if (!((String)pairs.getKey()).equals(str_class)) {
							Class other_class = ((Class)pairs.getValue());

							if (other_class.words.get(str_word) == null) {
								other_class.words.put(str_word, new Word(0));
							}
						}
					}
				}
			}

			c_class.training_count++;
		}
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Calculate
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public void calculate() {
		int email_count = 0;

		// count the total number of emails used.
		Iterator it = this.classes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			email_count += ((Class)pairs.getValue()).training_count;
		}

		// calculate the class prior
		it = this.classes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			((Class)pairs.getValue()).probability = (double)((Class)pairs.getValue()).training_count / email_count;
		}

		//		PREPROCESSING!
		// drop words which occur fewer than a given threshold(s) in both classes.
		HashMap<String, Integer> min_count = new HashMap<String, Integer>();
		min_count.put("ham", new Integer(20));
		min_count.put("spam", new Integer(15));
		// drop_words(min_count);


		// calculate p(w|c).
		it = this.classes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			Class cls = (Class)pairs.getValue();

			Iterator it2 = cls.words.entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry pairs2 = (Map.Entry)it2.next();
				String str_word = (String)pairs2.getKey();
				Word word = (Word)pairs2.getValue();

				word.probability = (double)(word.count+1) / (cls.dupe_word_count + words.size());
			}
		}
	}

	private void drop_words(HashMap<String, Integer> min_count) {

		HashSet<String> drop_words = null;
		HashMap<String, HashSet<String>> to_drop = new HashMap<String, HashSet<String>>();

		for (Map.Entry<String, Integer> count_entry : min_count.entrySet()) {
			String str_class = (String)count_entry.getKey();
			int count = ((Integer)count_entry.getValue()).intValue();

			Class cls = classes.get(str_class);
			to_drop.put(str_class, new HashSet<String>());

			for (Map.Entry<String, Word> word_entry : cls.words.entrySet()) {
				String str_word = word_entry.getKey();
				Word word = word_entry.getValue();

				if (word.count < count) {
					to_drop.get(str_class).add(str_word);
				}
			}
		}

		for (HashSet<String> to_drop_class : to_drop.values()) {
			if (drop_words == null) {
				drop_words = to_drop_class;
			} else {
				drop_words.retainAll(to_drop_class);
			}
		}

		// remove the offending words
		for (Class cls : classes.values()) {
			for (String word : drop_words) {
				cls.words.remove(word);
			}
		}

		// recalculate the dupe word count
		for (Map.Entry<String, Class> class_entry : classes.entrySet()) {
			String str_class = (String)class_entry.getKey();
			Class cls = (Class)class_entry.getValue();
			cls.dupe_word_count = 0;

			for (Word word : cls.words.values()) {
				cls.dupe_word_count += word.count;
			}
		}
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Serialization
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	// loading data saved in a file
	public void load_data(String path) {
		try {
			FileInputStream file_in = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(file_in);

			int length = in.readInt();
			for (int i = 0; i < length; i++) {
				classes.put( (String)in.readObject(), (Class)in.readObject() );
			}

			preprocessor = (ProcessEmailCallback)in.readObject();

			in.close();
			file_in.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException c) {
			// System.out.println("Class not found.");
			c.printStackTrace();
		}
	}

	// saving data to a file to be loaded later
	public void save_data(String path) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			out.writeInt(classes.size());
			for (Map.Entry<String, Class> entry : classes.entrySet()) {
				out.writeObject(entry.getKey());
				out.writeObject(entry.getValue());
			}

			out.writeObject(preprocessor);

			out.close();
			fileOut.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
