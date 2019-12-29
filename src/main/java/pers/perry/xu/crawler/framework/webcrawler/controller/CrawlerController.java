package pers.perry.xu.crawler.framework.webcrawler.controller;

import java.util.List;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;
import pers.perry.xu.crawler.framework.webcrawler.worker.CrawlerEngine;
import pers.perry.xu.crawler.framework.webcrawler.worker.WorkerType;

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
			this.configuration.postInitAfterConfiguration();
		} else {
			log.error(Logging.format("Configuration is invalid."));
			controllerStatus = CrawlerStatus.ConfigurationError;
		}
	}

	/**
	 * Start the web crawler with existing configuration.
	 */
	public void startCrawler() {
		if (controllerStatus == CrawlerStatus.Normal) {
			log.info(Logging.format("Web Crawler Plus is started."));
			CrawlerEngine engine = new CrawlerEngine(configuration);
			engine.startWorkers();

			List<String> seedList = configuration.getSeedList();
			for (String seed : seedList) {
				try { // insert seeds into both RESOURCE and SEED MQs
					MessageBroker.getOrCreateMessageQueueBroker(WorkerType.SeedWorker).addMessage(seed, null);
					MessageBroker.getOrCreateMessageQueueBroker(WorkerType.ResourceWorker).addMessage(seed, null);
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			log.error(Logging.format("Crawler is not started because controller is in status: {}", controllerStatus));
		}
	}
}
