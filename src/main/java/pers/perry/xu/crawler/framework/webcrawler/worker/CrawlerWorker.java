package pers.perry.xu.crawler.framework.webcrawler.worker;

import pers.perry.xu.crawler.framework.webcrawler.controller.CrawlerController;
import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

public class CrawlerWorker implements Runnable {

	private int threadIndex;
	private CrawlerController crawlerController;

	CrawlerWorker(int index, CrawlerController crawlerController) {
		this.threadIndex = index;
		this.crawlerController = crawlerController;

		Utils.print("Worker thread {} is created and running...", threadIndex);
	}

	public void run() {
		// we need to first make sure crawlerController is not null!
		while (true) {
			String nextTargetUrl = MessageBroker.getMessage();
			Utils.print("Worker thread {}: running crawler on next url: {}", threadIndex, nextTargetUrl);

			Utils.print("Worker thread {}: {} is done.", threadIndex, nextTargetUrl);
		}
	}
}
