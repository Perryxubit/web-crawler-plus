package pers.perry.xu.crawler.framework.webcrawler.utils;

public class Utils {

	public static void print(Object... out) {
		if (out.length == 0) {
			return;
		} else if (out.length == 1) {
			System.out.println(out[0].toString());
		} else {
			String log = (String) out[0];
			for (int i = 1; i < out.length; i++) {
				log = log.replaceFirst("\\{\\s*\\}", out[i].toString());
			}
			System.out.println(log);
		}
	}
}
