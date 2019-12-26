package pers.perry.xu.crawler.framework.webcrawler.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.log.CrawlerLog;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

@ToString
@AllArgsConstructor
@Log4j
public class CrawlerConfiguration {

	@Setter
	@Getter
	private WebPageParser parser;

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

	@Getter
	private CrawlerLog crawlerLogHandler;

	public enum DataOutputMode {
		PrintInConsole, DownloadToFiles
	}

	@Setter
	@Getter
	private DataOutputMode outputMode = DataOutputMode.PrintInConsole; // print in console by default

	public CrawlerConfiguration() {
		initConfiguration();
	}

	public void setMaxThreadNumber(int maxThreadNumber) {
		this.maxThreadNumber = maxThreadNumber;
		this.maxThreadNumberSeedWorker = maxThreadNumber / 5 == 0 ? 1 : maxThreadNumber / 5;
		this.maxThreadNumberResourceWorker = this.maxThreadNumber - this.maxThreadNumberSeedWorker;
	}

	public void setMaxThreadNumberSeedWorker(int maxThreadNumberSeedWorker) {
		if (maxThreadNumberSeedWorker < maxThreadNumber) {
			this.maxThreadNumberResourceWorker = this.maxThreadNumber - this.maxThreadNumberSeedWorker;
		}
	}

	public void setMaxThreadNumberResourceWorker(int maxThreadNumberResourceWorker) {
		if (maxThreadNumberResourceWorker < maxThreadNumber) {
			this.maxThreadNumberSeedWorker = this.maxThreadNumber - this.maxThreadNumberResourceWorker;
		}
	}

	public void addSeed(String url) {
		seedList.add(url);
	}

	public void clearSeeds() {
		seedList = new ArrayList<String>();
	}

	public void setWorkSpace(String path) {
		this.workspacePath = path;
		initWorkSpace();
	}

	private void initConfiguration() {
		seedList = new ArrayList<String>();
		// each configuration are only supporting one log handler
		crawlerLogHandler = new CrawlerLog();
	}

	private void initWorkSpace() {
		try {
			String baseUrl = workspacePath + File.separator + this.RUNTIME_WORKSPACE_DIR;
			runtimeBasePath = Paths.get(baseUrl);
			if (!Files.exists(runtimeBasePath)) { // init workspace directories
				Files.createDirectories(runtimeBasePath);
				Files.createDirectory(Paths.get(baseUrl + File.separator + RUNTIME_WORKSPACE_LOG));
				Files.createDirectory(Paths.get(baseUrl + File.separator + RUNTIME_WORKSPACE_OUTPUT));
				log.info("Workspace directories is initialized.");
			}
		} catch (IOException e) {
			log.error("Error when initializing configuration: " + e.getMessage());
		}
	}

	public boolean configurationIsValid() {
		if (this.maxThreadNumberResourceWorker + this.maxThreadNumberSeedWorker != this.maxThreadNumber) {
			log.error("Thread number configuration is wrong.");
			return false;
		} else if (seedList == null || seedList.size() == 0) {
			log.error("Seed list is empty.");
			return false;
		} else if (StringUtils.isEmpty(workspacePath)) {
			log.error("Workspace path is not set.");
			return false;
		} else if (!Files.exists(runtimeBasePath)) {
			log.error("Runtime directory (" + runtimeBasePath + ") does not exist.");
			return false;
		}
		log.info("Configuration check is passed.");
		return true;
	}
}
