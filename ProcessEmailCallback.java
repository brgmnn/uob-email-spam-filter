/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:		Callback.java
 *	author:			Daniel Bergmann
 *	email:			db0763@bristol.ac.uk
 *	description:
 **************************************************************************************************/

interface ProcessEmailCallback extends java.io.Serializable {
	String process_email(String email);
}