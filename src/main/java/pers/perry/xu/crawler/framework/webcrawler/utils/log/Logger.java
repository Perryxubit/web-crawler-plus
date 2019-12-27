package pers.perry.xu.crawler.framework.webcrawler.utils.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import pers.perry.xu.crawler.framework.webcrawler.utils.log.LogProperties.LogLevel;
import pers.perry.xu.crawler.framework.webcrawler.utils.log.LogProperties.OutputMode;

public class Logger {

	public static LogProperties Property;

	public static void debug(Object... args) {
		if (LogProperties.LoggingLevel == LogLevel.DEBUG || LogProperties.LoggingLevel == LogLevel.INFO
				|| LogProperties.LoggingLevel == LogLevel.WARN || LogProperties.LoggingLevel == LogLevel.ERROR) {
			outputLogContent("DEBUG: ", args);
		}
	}

	public static void info(Object... args) {
		if (LogProperties.LoggingLevel == LogLevel.INFO || LogProperties.LoggingLevel == LogLevel.WARN
				|| LogProperties.LoggingLevel == LogLevel.ERROR) {
			outputLogContent("INFO:  ", args);
		}
	}

	public static void warn(Object... args) {
		if (LogProperties.LoggingLevel == LogLevel.WARN || LogProperties.LoggingLevel == LogLevel.ERROR) {
			outputLogContent("WARN:  ", args);
		}
	}

	public static void error(Object... args) {
		if (LogProperties.LoggingLevel == LogLevel.ERROR) {
			outputLogContent("ERROR: ", args);
		}
	}

	private static void outputLogContent(String level, Object... args) {
		if (args.length == 0) {
			return;
		} else if (args.length == 1) {
			output(level + args[0].toString());
		} else {
			String log = (String) args[0];
			for (int i = 1; i < args.length; i++) { // replace each place holder
				log = log.replaceFirst("\\{\\s*\\}", args[i].toString());
			}
			output(level + log);
		}
	}

	private static void output(String content) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LogProperties.LoggingTimeFormat);
		String loggingTime = simpleDateFormat.format(new Date());

		if (LogProperties.Mode == OutputMode.PrintInConsole) {
			System.out.println("" + loggingTime + " " + content);
		} else if (LogProperties.Mode == OutputMode.PrintInFile) {

		}
	}
}
