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
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

@ToString
@Getter
@AllArgsConstructor
@Log4j
public class CrawlerConfiguration {

	@Setter
	private WebPageParser parser;

	@Setter
	private int threadCreateSleepTimeMS = 200;

	@Setter
	private int pageRetrieveSleepTimeMS = 500;

	private int maxThreadNumber = 5; // default is 5
	private int maxThreadNumberSeedWorker = 1;
	private int maxThreadNumberResourceWorker = 4;

	private String workspacePath = null;
	private Path logBasePath = null;
	private ArrayList<String> seedList;

	private final String RUNTIME_WORKSPACE_DIR = "wcpruntime";

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
//		if (!StringUtils.isEmpty(workspacePath)) {
//			initWorkSpace();
//		}
	}

	private void initWorkSpace() {
		try {
			logBasePath = Paths.get(workspacePath + File.separator + this.RUNTIME_WORKSPACE_DIR);
			if (!Files.exists(logBasePath)) {
				Files.createDirectories(logBasePath);
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
		} else if (!Files.exists(logBasePath)) {
			log.error("Runtime directory (" + logBasePath + ") does not exist.");
			return false;
		}
		log.info("Configuration check is passed.");
		return true;
	}
}
