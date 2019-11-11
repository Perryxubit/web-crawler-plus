package pers.perry.xu.crawler.framework.webcrawler.log;

import java.util.concurrent.ConcurrentSkipListSet;

public class CrawlerLog {

//	private Set<String> historySet = null;
	private ConcurrentSkipListSet<String> historySet;

	public CrawlerLog() {
		historySet = new ConcurrentSkipListSet<String>();
//		historySet = Collections.synchronizedSet(new HashSet<String>());
	}

	public boolean isInHistory(String url) {
		return historySet.contains(url);
	}

	public void addToHistory(String url) {
		this.historySet.add(url);
	}
}
