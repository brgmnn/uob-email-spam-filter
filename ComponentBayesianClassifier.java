/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:		ComponentBayesianClassifier.java
 *	author:			Daniel Bergmann
 *	email:			db0763@bristol.ac.uk
 *	description:
 **************************************************************************************************/
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class ComponentBayesianClassifier extends Classifier {
	private HashMap<String, Component> components = new HashMap<String, Component>();
	private ArrayList<String> class_names = new ArrayList<String>();
	public int training_count = 0;
	private boolean fixed_components = false;

	public ComponentBayesianClassifier() {}

	// ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
	//		add component
	// this is the whole point of the component bayesian classifier. the document is split in to
	// components such as subject and body. each is effectively given it's own bayesian classifier
	// and then the probabilities are combined. this function is used to add a component. it just
	// maps a string name and bayesian classifier together.
	public void add_component(String name, ProcessEmailCallback processor) {
		if (!fixed_components)
			components.put(name, new Component(processor));
	}

	public void set_component_weighting(String name, double weight) {
		components.get(name).weighting = weight;
	}

	// ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
	//		add class
	// adds a class to all the component bayesian classifiers. this locks the number of components
	// so after adding classes we cannot add more components.
	public void add_class(String name) {
		fixed_components = true;
		class_names.add(name);

		for (Component component : components.values()) {
			component.classifier.add_class(name);
		}
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Get probabilities
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	// returns the class prior. although there is a loop over components, we actually just grab the
	// first component as we don't care about which component we use as the class priors are all the
	// same as they do not depend on the contents, just the number of training emails.
	public double get_class_prior(String str_class) {
		double probability = 0.0;
		for (Component component : components.values()) {
			probability = component.classifier.get_class_prior(str_class);
			break;
		}
		return probability;
	}

	// returns the probability of a class given an email. This is almost classifying the email.
	public double get_p_class_given_email(String str_class, String str_email) {
		// double probability = get_class_prior(str_class);
		double probability = 0.0;

		for (Component component : components.values()) {
			probability += (component.weighting / get_component_length(str_email, component))
							* component.classifier.get_p_class_given_email(str_class, str_email);
		}
		return probability;
	}

	public int get_component_length(String str_email, Component component) {
		String[] words = component.classifier.preprocessor.process_email(str_email).split(" ");
		return words.length;
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Classify
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public String classify(String email) {
		String the_class = "";
		double max_prob = 0.0;
		double prob = 0.0;

		for (String str_class : class_names) {
			prob = get_p_class_given_email(str_class, email);

			if (prob > max_prob || the_class.length() == 0) {
				max_prob = prob;
				the_class = str_class;
			}
		}

		return the_class;
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Add training data
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public void add_training_email(String email, String str_class) {
		if (class_names.contains(str_class))
			for (Component component : components.values())
				component.classifier.add_training_email(email, str_class);
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Calculate
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public void calculate() {
		for (Component component : components.values()) {
			component.classifier.calculate();
		}
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Serialization
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public void load_data(String path) {
		try {
			FileInputStream file_in = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(file_in);

			// reads in the preprocessor callback function reference
			preprocessor = (ProcessEmailCallback)in.readObject();

			// reads in the components and their bayesian classifiers
			int length = in.readInt();
			for (int i = 0; i < length; i++) {
				components.put( (String)in.readObject(), (Component)in.readObject() );
			}

			// reads in the class names
			length = in.readInt();
			for (int i = 0; i < length; i++) {
				class_names.add((String)in.readObject());
			}

			// reads in the training count and the fixed components boolean (which is probs false)
			training_count = in.readInt();
			fixed_components = in.readBoolean();


			in.close();
			file_in.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException c) {
			c.printStackTrace();
		}
	}

	public void save_data(String path) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			// writes the preprocessor callback function reference.
			out.writeObject(preprocessor);

			// writes the components and their bayesian classifiers. should handle itself...
			out.writeInt(components.size());
			for (Map.Entry<String, Component> entry : components.entrySet()) {
				out.writeObject(entry.getKey());
				out.writeObject(entry.getValue());
			}

			// writes the class names
			out.writeInt(class_names.size());
			for (String class_name : class_names)
				out.writeObject(class_name);

			// writes the training count and fixed components bool.
			out.writeInt(training_count);
			out.writeBoolean(fixed_components);

			out.close();
			fileOut.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

/* ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~		Debugging
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~*/
	public String toString() {
		String info = "";
		for (Map.Entry<String, Component> entry : components.entrySet()) {
			info += "Component: "+entry.getKey()+"\n";
			info += entry.getValue().classifier;
			info += "\n\n";
		}

		for (String class_name : class_names) {
			info += "Overall '"+class_name+"' prior = "+get_class_prior(class_name)+"\n";
		}

		return info;
	}

}
