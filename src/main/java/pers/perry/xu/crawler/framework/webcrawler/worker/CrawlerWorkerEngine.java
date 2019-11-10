package pers.perry.xu.crawler.framework.webcrawler.worker;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pers.perry.xu.crawler.framework.webcrawler.controller.CrawlerController;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

public class CrawlerWorkerEngine {

	private CrawlerController crawlerController = null;;

	private Path outputPath = null;

	private ExecutorService threadPool;

	private int threadCreateInterval = 500;

	public CrawlerWorkerEngine(CrawlerController crawlerController, Path outputPath) {
		this.crawlerController = crawlerController;
		this.outputPath = outputPath;
	}

	/**
	 * start worker with n threads. (1 <= n <= 10)
	 * 
	 * @param n thread number
	 */
	public void startWorkers(int n) {
		if (crawlerController == null || outputPath == null) {
			Utils.print("ERROR: Need to input non-null crawlerController and outputPath.");
			return;
		}

		if (n < 1) {
			n = 1;
		} else if (n > 10) {
			n = 10;
		}

		Utils.print("Crawler: Starting {} threads...", n);
		threadPool = Executors.newFixedThreadPool(n);
		try {
			for (int i = 0; i < n; i++) {
				CrawlerWorker thread = new CrawlerWorker(i + 1, crawlerController);
				threadPool.execute(thread);

				Thread.sleep(threadCreateInterval);

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		threadPool.shutdown();
		Utils.print("Crawler: crawler thread pool is destroyed.");
	}
}
