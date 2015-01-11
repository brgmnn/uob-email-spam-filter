/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:		ComponentBayesianClassifier.java
 *	author:			Daniel Bergmann
 *	email:			db0763@bristol.ac.uk
 *	description:
 **************************************************************************************************/

public class Component implements java.io.Serializable {
	protected BayesianClassifier classifier = new BayesianClassifier();
	protected double weighting = 1.0;
	protected double average_component_length = 0;

	public Component(ProcessEmailCallback processor) {
		classifier.add_preprocessor_callback(processor);
	}
}