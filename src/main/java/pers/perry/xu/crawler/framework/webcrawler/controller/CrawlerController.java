package pers.perry.xu.crawler.framework.webcrawler.controller;

import java.util.List;

import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.worker.CrawlerWorkerEngine;

public class CrawlerController {

	private CrawlerConfiguration configuration;

	public CrawlerController(CrawlerConfiguration configuration) {
		this.configuration = configuration;
	}

	public void startCrawler() {
		CrawlerWorkerEngine engine = new CrawlerWorkerEngine(configuration);
		engine.startWorkers(configuration.getMaxThreadNumber());

		List<String> seedList = configuration.getSeedList();

		for (String seed : seedList) {
			MessageBroker.addMessage(seed);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
