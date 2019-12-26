package pers.perry.xu.crawler.framework.webcrawler.utils.log;

public class LogProperties {

	// logger configuration
	enum OutputMode {
		PrintInConsole, PrintInFile
	}

	enum LogLevel {
		ERROR, WARN, INFO, DEBUG, NONE
	}

	public static OutputMode Mode = OutputMode.PrintInConsole; // by default print in console

	public static LogLevel LoggingLevel = LogLevel.WARN; // by default using warning

	public static String LoggingTimeFormat = "yyyy-MM-dd HH:mm:ss";
}
