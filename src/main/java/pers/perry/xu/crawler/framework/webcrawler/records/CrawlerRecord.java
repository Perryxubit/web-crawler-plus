package pers.perry.xu.crawler.framework.webcrawler.records;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentSkipListSet;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;

@Log4j
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

	/**
	 * Check whether the URL has been handled before (whether in the crawler records
	 * or not).
	 * 
	 * @param url the given URL to check
	 * @return whether the URl has been handled
	 */
	public boolean isInHistory(String url) {
		return historySet.contains(url);
	}

	/**
	 * Mark the URl in the crawler record which means has been handled before.
	 * 
	 * @param url the URL to be marked
	 */
	public void addToHistory(String url) {
		this.historySet.add(url);
	}

	/**
	 * Read the Record file and load all records into memory.
	 */
	private void loadRecordLogFile() {
		Path recordFilePath = Paths
				.get(configuration.getWcpLogPath() + File.separator + configuration.getWcpLogRecordFile());

		if (configuration.getWcpLogPath() != null && Files.exists(configuration.getWcpLogPath())) {
			try {
				if (!Files.exists(recordFilePath)) {
					Files.createFile(recordFilePath);
				} else {
					// load existing entries into historySet
//					try (BufferedReader br = new BufferedReader(new FileReader(recordFilePath.toString()))) {
//						String str = null;
//						while ((str = br.readLine()) != null) {
//							// …
//						}
//					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			log.warn(Logging.format("wcp record log path does not exist, no records loaded."));
		}
	}
}
