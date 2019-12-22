package pers.perry.xu.crawler.framework.webcrawler.configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jsoup.nodes.Element;

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

	@Setter
	private String outputBasePath = null;

	private Path logBasePath = null;

	private ArrayList<String> seedList;

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

	private void initConfiguration() {
		seedList = new ArrayList<String>();
		logBasePath = Paths.get(uri)outputBasePath + File.separator + "wcpruntime";
		Files.exists(path, options)
	}

	public boolean configurationIsValid() {
		if (this.maxThreadNumberResourceWorker + this.maxThreadNumberSeedWorker != this.maxThreadNumber) {
			log.error("Thread number configuration is wrong.");
			return false;
		} else if (seedList == null || seedList.size() == 0) {
			log.error("Seed list is empty.");
			return false;
		} else if ()
		log.info("Configuration check is passed.");
		return true;
	}
}
