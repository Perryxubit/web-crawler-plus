package pers.perry.xu.crawler.framework.webcrawler.controller;

import java.util.List;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.worker.CrawlerEngine;

@Log4j
public class CrawlerController {

	private CrawlerConfiguration configuration;

	public static enum CrawlerStatus {
		ConfigurationError, Normal
	}

	private CrawlerStatus controllerStatus = CrawlerStatus.Normal;

	public CrawlerController(CrawlerConfiguration configuration) {
		if (configuration.configurationIsValid()) {
			this.configuration = configuration;
		} else {
			log.error("Configuration is invalid.");
			controllerStatus = CrawlerStatus.ConfigurationError;
		}
	}

	public void startCrawler() {
		if (controllerStatus == CrawlerStatus.Normal) {
			CrawlerEngine engine = new CrawlerEngine(configuration);
			engine.startWorkers(configuration.getMaxThreadNumber());

			List<String> seedList = configuration.getSeedList();

			for (String seed : seedList) {
				try {
					MessageBroker.addMessage(seed);
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			log.error("Crawler is not started because controller is in status: " + controllerStatus);
		}

	}

}
