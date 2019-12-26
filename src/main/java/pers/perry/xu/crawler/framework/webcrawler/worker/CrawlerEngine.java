package pers.perry.xu.crawler.framework.webcrawler.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

@Log4j
public class CrawlerEngine {

	private ExecutorService threadPoolSeeds;
	private ExecutorService threadPoolResources;
//	private ExecutorService threadPool;

	private CrawlerConfiguration configuration;

	public CrawlerEngine(CrawlerConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * start worker with n threads. (1 <= n <= 20)
	 * 
	 * @param n thread number
	 */
	public void startWorkers() {
		int maxThreadsNr = configuration.getMaxThreadNumber();
		int maxSeedsThreadsNr = configuration.getMaxThreadNumberSeedWorker();
		int maxResourceThreadsNr = configuration.getMaxThreadNumberResourceWorker();

		if (maxThreadsNr <= 2) {
			maxThreadsNr = 2;
			maxSeedsThreadsNr = 1;
			maxResourceThreadsNr = 1;
		} else if (maxThreadsNr >= 20) {
			maxThreadsNr = 20;
			maxSeedsThreadsNr = 4;
			maxResourceThreadsNr = 16;
		}

		Utils.print("Crawler: Starting {} threads...", maxThreadsNr);

		// create seeds thread pool
		int index = 0;
		threadPoolSeeds = Executors.newFixedThreadPool(maxSeedsThreadsNr);
		try {
			for (; index < maxSeedsThreadsNr; index++) {
				CrawlerWorker worker = new CrawlerWorker(index + 1, WorkerType.SeedWorker, configuration.getParser(),
						configuration);
				threadPoolSeeds.execute(worker);
				Thread.sleep(configuration.getThreadCreateSleepTimeMS());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			threadPoolSeeds.shutdown();
		}

		// create resources thread pool
		threadPoolResources = Executors.newFixedThreadPool(maxResourceThreadsNr);
		try {
			for (int i = 0; i < maxResourceThreadsNr; i++) {
				CrawlerWorker worker = new CrawlerWorker(index + i + 1, WorkerType.ResourceWorker,
						configuration.getParser(), configuration);
				threadPoolResources.execute(worker);
				Thread.sleep(configuration.getThreadCreateSleepTimeMS());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			threadPoolResources.shutdown();
		}
		log.info("Crawler thread pools are destroyed.");
	}
}
