package pers.perry.xu.crawler.framework.webcrawler.configuration;

import java.io.File;
import java.util.ArrayList;

import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

public class CrawlerConfiguration {

	private int threadCreateSleepTimeMS = 100;

	private int pageRetrieveSleepTimeMS = 500;

	private String outputBasePath = null;

	private String logBasePath = outputBasePath + File.separator + "wcpruntime";

	private ArrayList<String> seedList;

	public CrawlerConfiguration() {
		seedList = new ArrayList<String>();
	}

	public void addSeed(String url) {
		seedList.add(url);
	}

	public void clearSeeds() {
		seedList = new ArrayList<String>();
	}

	private WebPageParser parser;

	public void setParser(WebPageParser parser) {
		this.parser = parser;
	}

	public WebPageParser getParser() {
		return parser;
	}

	private int maxThreadNumber = 10;

	public int getMaxThreadNumber() {
		return maxThreadNumber;
	}

	public void setMaxThreadNumber(int maxThreadNumber) {
		this.maxThreadNumber = maxThreadNumber;
	}

	// other setter/getter
	public int getThreadCreateSleepTimeMS() {
		return threadCreateSleepTimeMS;
	}

	public void setThreadCreateSleepTimeMS(int threadCreateSleepTimeMS) {
		this.threadCreateSleepTimeMS = threadCreateSleepTimeMS;
	}

	public int getPageRetrieveSleepTimeMS() {
		return pageRetrieveSleepTimeMS;
	}

	public void setPageRetrieveSleepTimeMS(int pageRetrieveSleepTimeMS) {
		this.pageRetrieveSleepTimeMS = pageRetrieveSleepTimeMS;
	}

	public String getOutputBasePath() {
		return outputBasePath;
	}

	public void setOutputBasePath(String outputBasePath) {
		this.outputBasePath = outputBasePath;
	}

	public String getLogBasePath() {
		return logBasePath;
	}

	public void setLogBasePath(String logBasePath) {
		this.logBasePath = logBasePath;
	}

	public ArrayList<String> getSeedList() {
		return seedList;
	}

	public void setSeedList(ArrayList<String> seedList) {
		this.seedList = seedList;
	}
}
