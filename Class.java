/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	Word.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.util.HashMap;

public class Class implements java.io.Serializable {
	public double probability = 0.0;
	public int training_count = 0;
	public int dupe_word_count = 0;

	public HashMap<String, Word> words = new HashMap<String, Word>();
}
