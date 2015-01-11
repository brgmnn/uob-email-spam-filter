/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	Word.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/

public class Word implements java.io.Serializable, java.lang.Comparable<Word> {
	public double probability = 0.0;
	public int count = 1;

	public Word() {}

	public Word(int count) {
		this.count = count;
	}

	public int compareTo(Word word) {
		return count - word.count;
	}

	public String toString() {
		return ""+count;
	}
}
