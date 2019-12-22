package pers.perry.xu.crawler.framework.webcrawler.controller;

import java.util.List;

import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.worker.CrawlerEngine;

public class CrawlerController {

	private CrawlerConfiguration configuration;

	public CrawlerController(CrawlerConfiguration configuration) {
		this.configuration = configuration;
	}

	public void startCrawler() {
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
	}
}
