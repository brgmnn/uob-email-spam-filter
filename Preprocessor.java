/***************************************************************************************************
 *		Introduction to Machine Learning
 *			Spam Filter - part 2
 * ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 *	filename:	Preprocessor.java
 *	author:		Daniel Bergmann
 *	email:		db0763@bristol.ac.uk
 **************************************************************************************************/
import java.util.Arrays;

public class Preprocessor implements ProcessEmailCallback {

	public static class PassThrough implements ProcessEmailCallback {
		public String process_email(String email) {
			return email;
		}
	}

	public static class HeaderSubject implements ProcessEmailCallback {
		public String process_email(String email) {
			String subject = Preprocessor.get_header_field(email, "Subject:");
			subject = subject.replaceAll("\\s", " ");
			subject = subject.replaceAll("[^A-Za-z0-9|\\n|\\r| ]", "");
			// subject = Preprocessor.filter_words_by_length(subject, 3, 15);
			return subject;
		}
	}

	public static class Body implements ProcessEmailCallback {
		public String process_email(String email) {
			String body = Preprocessor.strip_header(email);
			//body = body.replaceAll("[\\s]", " ");
			body = body.replaceAll("[\r|\r\n|\n\r]", "\n");
			body = body.replaceAll("[ \t\f]", " ");
			body = body.replaceAll("[^A-Za-z0-9|\\n|\\r| ]", "");
			// body = Preprocessor.filter_words_by_length(body, 3, 15);
			// body = Preprocessor.filter_words("");
			return body;
		}
	}

	public static class Subject2Grams implements ProcessEmailCallback {
		public String process_email(String email) {
			String subject = new Preprocessor.HeaderSubject().process_email(email);
			StringBuffer new_subject = new StringBuffer();

			String[] words = subject.split(" ");
			for (int i = 0; i < words.length; i++) {
				if ((i+1) < words.length)
					new_subject.append(words[i]+"."+words[i+1]+" ");
			}
			return new_subject.toString();
		}
	}

	public static class Body2Grams implements ProcessEmailCallback {
		public String process_email(String email) {
			String body = new Preprocessor.Body().process_email(email);
			StringBuffer new_body = new StringBuffer();

			String[] words = body.split(" ");
			for (int i = 0; i < words.length; i++) {
				if ((i+1) < words.length)
					new_body.append(words[i]+"."+words[i+1]+" ");
			}
			return new_body.toString();
		}
	}

	public static class Subject3Grams implements ProcessEmailCallback {
		public String process_email(String email) {
			String subject = new Preprocessor.HeaderSubject().process_email(email);
			StringBuffer new_subject = new StringBuffer();

			String[] words = subject.split(" ");
			for (int i = 0; i < words.length; i++) {
				if ((i+2) < words.length)
					new_subject.append(words[i]+"."+words[i+1]+"."+words[i+2]+" ");
			}
			return new_subject.toString();
		}
	}

	public static class Body3Grams implements ProcessEmailCallback {
		public String process_email(String email) {
			String body = new Preprocessor.Body().process_email(email);
			StringBuffer new_body = new StringBuffer();

			String[] words = body.split(" ");
			for (int i = 0; i < words.length; i++) {
				if ((i+2) < words.length)
					new_body.append(words[i]+"."+words[i+1]+"."+words[i+2]+" ");
			}
			return new_body.toString();
		}
	}

	public static class EmailAddresses implements ProcessEmailCallback {
		public String process_email(String email) {
			String from = Preprocessor.get_header_field(email, "From:");
			from = from.toLowerCase();
			from = from.replaceAll("[@&]", " ");
			from = from.replaceAll("[<>\"'\\(\\)]", "");
			//from = from.replaceAll("[0-9]{3,}", "")
			return from;
		}
	}	

	public Preprocessor() {}

	public String process_email(String email) {
		StringBuffer processed_email = new StringBuffer(email.length());
		String str_email = email;
		
		// strip header
		// str_email = Preprocessor.strip_header(email);
		// String str_header = Preprocessor.scrape_header(email);
		// processed_email.append(str_header);

		// strip certain words
		//email = Preprocessor.filter_words(email, "i");

		// filter words by length
		// email = Preprocessor.filter_words_by_length(email, 2, -1);

		// strip certain characters
		// email = str_email.replaceAll("[.,:;]", "");
		//email = str_email.replaceAll("[^A-Za-z0-9|\\n|\\r| ]", "");

		// make certain characters into their own words
		//email = str_email.replaceAll("[?!]", " $0 ");
		// email.toLowerCase();


		// String[] email_words = str_email.split("[\\p{Punct}\\s]");
		String[] email_words = str_email.split("\\s");
		//String[] email_words = str_email.split(" ");

		for (int i = 0; i < email_words.length; i++) {
			// if ((i+1) < email_words.length)
			// 	processed_email.append(email_words[i]+" "+email_words[i]+"."+email_words[i+1]+" ");
			// if ((i+1) < email_words.length)
			// 	processed_email.append(email_words[i]+"."+email_words[i+1]+" ");
			// else
				processed_email.append(email_words[i]+" ");
		}

		return processed_email.toString();
	}

	// strip email headers.
	public static String strip_header(String email) {
		String[] header_and_body = email.split("\n\n", 2);
		return (header_and_body.length == 2) ? header_and_body[1] : email;
	}

	// strip email body.
	public static String strip_body(String email) {
		String[] header_and_body = email.split("\n\n", 2);
		return (header_and_body.length == 2) ? header_and_body[0] : email;
	}

	// scrape header information
	public static String scrape_header(String email) {
		String[] header_and_body = email.split("\n\n", 2);

		if (header_and_body.length == 2) {
			String header = header_and_body[0];

			String[] fields = header.split("\n\\p{Graph}");

			//System.out.println(Arrays.toString(fields));
			StringBuffer scraped = new StringBuffer();

			for (String field : fields) {
				if (field.startsWith("Subject:"))
					for (int i = 0; i < 1; i++)
						scraped.append(field.substring(8)+" ");
				else if (field.startsWith("To:"))
					scraped.append(field.substring(3)+" ");
				else if (field.startsWith("Return-Path:"))
					scraped.append(field.substring(12)+" ");
				else if (field.startsWith("From:"))
					scraped.append(field.substring(5)+" ");
				else if (field.startsWith("Sender:"))
					scraped.append(field.substring(7)+" ");
			}
			return scraped.toString();
		}
		return "";
	}

	public static String get_header_field(String email, String wanted_field) {
		String[] header_and_body = email.split("\n\n", 2);

		if (header_and_body.length == 2) {
			String header = header_and_body[0];
			String[] fields = header.split("\n(?=\\p{Graph})");
			
			wanted_field = wanted_field.toLowerCase();

			for (String field : fields)
				if (field.toLowerCase().startsWith(wanted_field))
					return field.substring(wanted_field.length());
		}
		return "";
	}

	// at the moment just letters. "[a-zA-Z]"
	public static String filter_words(String email, String regex) {
		String[] words = email.split(" ");
		String stripped_email = "";

		for (String word : words) {
			if (word.matches(regex)) {
				stripped_email += word+" ";
			}
		}

		return stripped_email;
	}

	// filter words by length
	public static String filter_words_by_length(String email, int min) {
		return Preprocessor.filter_words_by_length(email, min, -1);
	}

	public static String filter_words_by_length(String email, int min, int max) {

		if (min != -1 && max != -1) {
			String[] words = email.split(" ");
			String stripped_email = "";

			for (String word : words) {
				if (word.length() >= min && word.length() <= max) {
					stripped_email += word+" ";
				}
			}

			return stripped_email;
		} else if ( min != -1 ) {
			String[] words = email.split(" ");
			String stripped_email = "";

			for (String word : words) {
				if (word.length() >= min) {
					stripped_email += word+" ";
				}
			}

			return stripped_email;
		} else if ( max != -1 ) {
			String[] words = email.split(" ");
			String stripped_email = "";

			for (String word : words) {
				if (word.length() <= max) {
					stripped_email += word+" ";
				}
			}

			return stripped_email;
		} else {
			return email;
		}
	}
}