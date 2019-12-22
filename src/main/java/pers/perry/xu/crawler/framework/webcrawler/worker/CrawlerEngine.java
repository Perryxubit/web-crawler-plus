package pers.perry.xu.crawler.framework.webcrawler.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.log.CrawlerLog;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

public class CrawlerEngine {

	private ExecutorService threadPool;

	private CrawlerConfiguration configuration;

	private CrawlerLog crawlerLogging;

	public CrawlerEngine(CrawlerConfiguration configuration) {
		this.configuration = configuration;

		this.crawlerLogging = new CrawlerLog();
	}

	public enum WorkerType {
		SeedWorker, // only working on adding new seeds
		ResourceWorker // only working on resource spider
	}

	/**
	 * start worker with n threads. (1 <= n <= 10)
	 * 
	 * @param n thread number
	 */
	public void startWorkers(int n) {
		if (n <= 1) {
			n = 1;
		} else if (n >= 10) {
			n = 10;
		}

		Utils.print("Crawler: Starting {} threads...", n);
		threadPool = Executors.newFixedThreadPool(n);
		try {
			for (int i = 0; i < n; i++) {
				CrawlerWorker thread = new CrawlerWorker(i + 1, configuration.getParser(), crawlerLogging);
				threadPool.execute(thread);

				Thread.sleep(configuration.getThreadCreateSleepTimeMS());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		threadPool.shutdown();
		Utils.print("Crawler: crawler thread pool is destroyed.");
	}
}
