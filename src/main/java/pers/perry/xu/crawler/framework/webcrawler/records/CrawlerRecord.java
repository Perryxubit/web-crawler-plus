package pers.perry.xu.crawler.framework.webcrawler.records;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentSkipListSet;

import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;

public class CrawlerRecord {

//	private Set<String> historySet = null;
	private ConcurrentSkipListSet<String> historySet;

	private CrawlerConfiguration configuration;

	public CrawlerRecord(CrawlerConfiguration configuration) {
		historySet = new ConcurrentSkipListSet<String>();
//		historySet = Collections.synchronizedSet(new HashSet<String>());

		this.configuration = configuration;
		this.loadRecordLogFile();
	}

	public boolean isInHistory(String url) {
		return historySet.contains(url);
	}

	public void addToHistory(String url) {
		this.historySet.add(url);
	}

	private void loadRecordLogFile() {
		Path recordFilePath = Paths
				.get(configuration.getWcpLogPath() + File.separator + configuration.getWcpLogRecordFile());
		try {
			if (!Files.exists(recordFilePath)) {
				Files.createFile(recordFilePath);
			} else {
				// load existing entries into historySet
				try (BufferedReader br = new BufferedReader(new FileReader(recordFilePath.toString()))) {
					String str = null;
					while ((str = br.readLine()) != null) {
						// …
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
