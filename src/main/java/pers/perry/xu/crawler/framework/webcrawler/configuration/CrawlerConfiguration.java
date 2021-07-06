package pers.perry.xu.crawler.framework.webcrawler.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;
import pers.perry.xu.crawler.framework.webcrawler.records.CrawlerRecord;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;

@ToString
@Log4j
public class CrawlerConfiguration {

	@Setter
	@Getter
	private WebPageParser parser;

	@Setter
	@Getter
	private boolean enableCrawlingRecording = true;

	@Setter
	@Getter
	private int threadCreateSleepTimeMS = 200;

	@Setter
	@Getter
	private int pageRetrieveSleepTimeMS = 500;

	@Setter
	@Getter
	private int mediaDownloadTimeoutMS = 5000;

	@Setter
	@Getter
	private int downloadWaitTimeMS = 1000;

	@Getter
	private int maxThreadNumber = 5; // default is 5
	@Getter
	private int maxThreadNumberSeedWorker = 1;
	@Getter
	private int maxThreadNumberResourceWorker = 4;

	@Getter
	private String workspacePath = null;

	private Path runtimeBasePath = null;
	@Getter
	private Path wcpLogPath = null;
	@Getter
	private Path wcpOutputPath = null;
	@Getter
	private ArrayList<String> seedList;

	private final String RUNTIME_WORKSPACE_DIR = "wcpruntime";
	private final String RUNTIME_WORKSPACE_OUTPUT = "output";
	private final String RUNTIME_WORKSPACE_LOG = "wcplog";

	private final int MAX_THREAD_Number = 10;

	@Getter
	private final String wcpLogRecordFile = "wcp-records.dat";

	@Getter
	private CrawlerRecord crawlerRecordHandler;

	public enum DataOutputMode {
		PrintInConsole, DownloadToFiles, DoNothing
	}

	@Setter
	@Getter
	private DataOutputMode outputMode = DataOutputMode.PrintInConsole; // print result in console by default

	public CrawlerConfiguration() {
		initConfiguration();
	}

	/**
	 * Set max worker thread number N, Seed workers should be set to N/5 by default,
	 * and others are Resource workers.
	 *
	 * @param maxThreadNumber the max number
	 */
	public void setMaxThreadNumber(int maxThreadNumber) {
		this.maxThreadNumber = maxThreadNumber;
		this.maxThreadNumberSeedWorker = maxThreadNumber / 5 == 0 ? 1 : maxThreadNumber / 5;
		this.maxThreadNumberResourceWorker = this.maxThreadNumber - this.maxThreadNumberSeedWorker;
	}

	/**
	 * Set Seed workers number N, and reset Resource workers number according to N.
	 *
	 * @param maxThreadNumberSeedWorker the max seed workers numbers
	 */
	public void setMaxThreadNumberSeedWorker(int maxThreadNumberSeedWorker) {
		if (maxThreadNumberSeedWorker < maxThreadNumber) {
			this.maxThreadNumberResourceWorker = this.maxThreadNumber - this.maxThreadNumberSeedWorker;
		}
	}

	/**
	 * Set Resource workers number N, and reset Seed workers number according to N.
	 *
	 * @param maxThreadNumberResourceWorker the max resource workers numbers
	 */
	public void setMaxThreadNumberResourceWorker(int maxThreadNumberResourceWorker) {
		if (maxThreadNumberResourceWorker < maxThreadNumber) {
			this.maxThreadNumberSeedWorker = this.maxThreadNumber - this.maxThreadNumberResourceWorker;
		}
	}

	/**
	 * Append new seed web URL.
	 *
	 * @param url
	 */
	public void addSeed(String url) {
		seedList.add(url);
	}

	/**
	 * Clear all web seeds URL added using addSeed(String url).
	 */
	public void clearSeeds() {
		seedList = new ArrayList<String>();
	}

	/**
	 * Set the work space path Work space contains the record logs, output, and
	 * other runtime files.
	 *
	 * @param path the crawler work space path
	 */
	public void setWorkSpace(String path) {
		this.workspacePath = path;
		initWorkSpace();
	}

	/**
	 * Some configuration initialization when creating configuration.
	 */
	private void initConfiguration() {
		seedList = new ArrayList<String>();
		// TODO: add other initialization here...
	}

	/**
	 * Post initialization after configuration is done. This function needs to be
	 * called after configuration is done, and before startCrawler() of Controller.
	 */
	public void postInitAfterConfiguration() {
		// each configuration are only supporting one log handler
		crawlerRecordHandler = new CrawlerRecord(this);
		// TODO: other configurations needs to be added after all configurations are set
	}

	/**
	 * Initialize the work space sub directories, should be called after
	 * setWorkSpace(String path).
	 */
	private void initWorkSpace() {
		try {
			String baseUrl = workspacePath + File.separator + this.RUNTIME_WORKSPACE_DIR;
			// create runtime base
			this.runtimeBasePath = Paths.get(baseUrl);
			if (!Files.exists(runtimeBasePath)) { // init workspace directories
				Files.createDirectories(runtimeBasePath);
			}
			// create log folder
			this.wcpLogPath = Paths.get(baseUrl + File.separator + RUNTIME_WORKSPACE_LOG);
			if (!Files.exists(wcpLogPath)) {
				Files.createDirectory(wcpLogPath);
			}
			// create output folder
			this.wcpOutputPath = Paths.get(baseUrl + File.separator + RUNTIME_WORKSPACE_OUTPUT);
			if (!Files.exists(wcpOutputPath)) {
				Files.createDirectory(wcpOutputPath);
			}
			log.info(Logging.format("Workspace directories are initialized."));
		} catch (IOException e) {
			log.error(Logging.format("Error when initializing configuration: {}", e.getMessage()));
		}
	}

	/**
	 * Check whether the current configuration is valid or not.
	 *
	 * @return whether the configuration is valid
	 */
	public boolean configurationIsValid() {
		if (this.maxThreadNumberResourceWorker + this.maxThreadNumberSeedWorker != this.maxThreadNumber) {
			log.error(Logging.format("Confirguation error: Thread number configuration is wrong."));
			return false;
		} else if (seedList == null || seedList.size() == 0) {
			log.error(Logging.format("Confirguation error: Seed list is empty."));
			return false;
		} else if (StringUtils.isEmpty(workspacePath)) {
			log.error(Logging.format("Confirguation error: Workspace path is not set correctly."));
			return false;
		} else if (!Files.exists(runtimeBasePath)) {
			log.error(Logging.format("Confirguation error: Runtime directory ({}) does not exist.", runtimeBasePath));
			return false;
		}
		log.info(Logging.format("Configuration check is passed."));
		return true;
	}
}
