/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	filter.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.lang.Math;

public class Statistics {
	private Statistics() {}

	public static double sum( double[] nums ) {
		double sum = 0.0;

		for (int i = 0; i < nums.length; i++)
			sum += nums[i];

		return sum;
	}

	public static double mean( double[] nums ) {
		return Statistics.sum(nums) / nums.length;
	}

	public static double stdev( double[] nums ) {
		double mean = Statistics.mean(nums);
		double[] diff = new double[nums.length];

		for (int i = 0; i < diff.length; i++) {
			diff[i] = Math.pow((nums[i] - mean), 2);
		}

		return (diff.length / (diff.length-1)) * Math.sqrt(Statistics.mean(diff));
	}
}