package pers.perry.xu.crawler.framework.webcrawler.records;

import java.util.concurrent.ConcurrentSkipListSet;

public class CrawlerRecord {

//	private Set<String> historySet = null;
	private ConcurrentSkipListSet<String> historySet;

	public CrawlerRecord() {
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
