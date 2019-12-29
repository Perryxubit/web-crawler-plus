package pers.perry.xu.crawler.framework.webcrawler.utils;

public class Logging {

	public static String format(Object... args) {
		String log;
		if (args.length == 0 || args == null) {
			log = "";
		} else if (args.length == 1) {
			return (String) args[0];
		} else {
			log = (String) args[0];
			for (int i = 1; i < args.length; i++) { // replace each place holder
				log = log.replaceFirst("\\{\\s*\\}", args[i].toString());
			}
			return log;
		}

		return log;
	}
}
