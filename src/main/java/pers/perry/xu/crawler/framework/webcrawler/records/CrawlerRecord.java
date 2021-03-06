package pers.perry.xu.crawler.framework.webcrawler.records;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentSkipListSet;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.records.async.AsyncFileIO4MultiThreads;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;
import pers.perry.xu.crawler.framework.webcrawler.worker.WorkerType;

@Log4j
public class CrawlerRecord {

	private ConcurrentSkipListSet<String> historySeedsSet;
	private ConcurrentSkipListSet<String> historyResourcesSet;
	private AsyncFileIO4MultiThreads asyncFilehandler;
	private CrawlerConfiguration configuration;
	private boolean enableRecording;

	public CrawlerRecord(CrawlerConfiguration configuration) {
		historySeedsSet = new ConcurrentSkipListSet<String>();
		historyResourcesSet = new ConcurrentSkipListSet<String>();

		this.configuration = configuration;
		this.enableRecording = this.configuration.isEnableCrawlingRecording();
		this.loadRecordLogFile();
		this.asyncFilehandler = new AsyncFileIO4MultiThreads(configuration.getWcpLogPath().toString(),
				configuration.getWcpLogRecordFile());
	}

	/**
	 * Check whether the URL has been handled before (whether in the crawler records
	 * or not).
	 * 
	 * @param url the given URL to check
	 * @return whether the URl has been handled
	 */
	public boolean isInHistory(String url, WorkerType workerType) {
		if(!this.enableRecording) { // always run crawler (not in history) if the recording is disabled.
			return false;
		}

		switch (workerType) {
		case SeedWorker:
			return historySeedsSet.contains(url);
		case ResourceWorker:
			return historyResourcesSet.contains(url);
		default:
			log.error(Logging.format("{} is not supported.", workerType));
			return false;
		}

	}

	/**
	 * Mark the URl in the crawler record which means has been handled before.
	 * 
	 * @param url the URL to be marked
	 */
	public void addToHistory(String url, WorkerType workerType, String threadId) {
//		if(!this.enableRecording) { // do not add anything if recording is disabled.
//			return;
//		}

		switch (workerType) {
		case SeedWorker:
			if (!historySeedsSet.contains(url)) {
				historySeedsSet.add(url);
				// make sure no repeated entries written to file
				asyncFilehandler.appendToFileWithBlockingAsync(workerType, threadId, url);
			}
			break;
		case ResourceWorker:
			if (!historyResourcesSet.contains(url)) {
				historyResourcesSet.add(url);
				// make sure no repeated entries written to file
				asyncFilehandler.appendToFileWithBlockingAsync(workerType, threadId, url);
			}
			break;
		default:
			log.error(Logging.format("{} is not supported.", workerType));
			break;
		}
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
					try (BufferedReader br = new BufferedReader(new FileReader(recordFilePath.toString()))) {
						String str = null;
						while ((str = br.readLine()) != null) {
							// entry example:
							// SeedWorker##1##message1
							// ResourceWorker##2##message2
							String[] list = str.split("##");
							if (list.length == 3) {
								if (list[0].toLowerCase().startsWith("seed")) {
									historySeedsSet.add(list[2]);
								} else if (list[0].toLowerCase().startsWith("resource")) {
									historyResourcesSet.add(list[2]);
								}
							}
							str = br.readLine();
						}
					}
					log.info("Load record file successfully.");
				}
			} catch (IOException e) {
				log.error(Logging.format("Error happened when loading record log file, error: {}", e.getMessage()));
			}
		} else {
			log.warn(Logging.format("wcp record log path does not exist, no records loaded."));
		}
	}
}
