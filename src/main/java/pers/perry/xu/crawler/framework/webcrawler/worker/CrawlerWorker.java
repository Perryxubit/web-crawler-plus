package pers.perry.xu.crawler.framework.webcrawler.worker;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.log.CrawlerLog;
import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

@Log4j
public class CrawlerWorker implements Runnable {

	private int threadIndex;
	private WebPageParser pageParser;
	private CrawlerLog crawlerLogging;
	private WorkerType workerType;

	private static int QUEUE_FULL_WAIT = 5000;

	CrawlerWorker(int index, WorkerType type, WebPageParser pageParser, CrawlerLog crawlerLogging) {
		this.threadIndex = index;
		this.workerType = type;
		this.pageParser = pageParser;
		this.crawlerLogging = crawlerLogging;

		Utils.print("Worker thread {} ({}) is created and running...", threadIndex, type);
	}

	public void run() {
		// we need to first make sure crawlerController is not null!
		while (true) {
//			String nextTargetUrl = workerType == WorkerType.SeedWorker
//					? MessageBroker.getMessageWithWaiting(threadIndex)
//					: MessageBroker.getMessage(threadIndex);
			String nextTargetUrl = MessageBroker.getMessage(threadIndex);

			crawlerLogging.addToHistory(nextTargetUrl);
			Utils.print("Worker thread {}: running crawler on next url: {}", threadIndex, nextTargetUrl);

			try {
				// https://www.ibm.com/developerworks/cn/java/j-lo-jsouphtml/index.html
				Document doc = Jsoup.connect(nextTargetUrl).get();
				String title = doc.title();

				Utils.print("# Worker thread {}: Title {}", threadIndex, title);

				WebPage page = new WebPage();
				page.setWebHead(doc.head());
				page.setWebBody(doc.body());
				page.setWebTitle(doc.title());
				page.setWebUrl(nextTargetUrl);

				parseWebPage(page);
			} catch (Exception e) {
				Utils.print("error: {}", e.getMessage());
			}
			Utils.print("Worker thread {}: {} is done.", threadIndex, nextTargetUrl);

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void parseWebPage(WebPage page) {
		// check if we have url contained in the current page which needs to be added in
		// to MQ. -> if yes, add to the MQ
		List<String> urlList = pageParser.getSeedUrlsList(page.getWebBody());
		if (urlList == null || urlList.size() == 0) {
			return;
		}
		for (String url : urlList) {
			if (!crawlerLogging.isInHistory(url)) { // only add new url
				Utils.print("Worker thread {}: sub url seed added {}", threadIndex, url);
				addToMQOrWait(url);
				crawlerLogging.addToHistory(url);
			}
		}

		// check if we have pageParser.visitTextPattern()
		// TODO:

		// check if we have pageParser.visitPicturePattern()
		List<String> picList = pageParser.getPicturesUrlsList(page.getWebBody());
		if (picList != null && picList.size() > 0) {
			for (String pic : picList) {
				Utils.print("### Worker thread {}: pic - {}", threadIndex, pic);
			}
		}
	}

//	private void parseWebPage(WebPage page) {
//		switch (workerType) {
//		case SeedWorker:
//			// check if we have url contained in the current page which needs to be added in
//			// to MQ. -> if yes, add to the MQ
//			List<String> urlList = pageParser.getSeedUrlsList(page.getWebBody());
//			if (urlList == null || urlList.size() == 0) {
//				return;
//			}
//			for (String url : urlList) {
//				if (!crawlerLogging.isInHistory(url)) { // only add new url
//					Utils.print("Worker thread {}: sub url seed added {}", threadIndex, url);
//					addToMQOrWait(url);
//					crawlerLogging.addToHistory(url);
//				}
//			}
//			break;
//		case ResourceWorker:
//			// check if we have pageParser.visitTextPattern()
//			// TODO:
//
//			// check if we have pageParser.visitPicturePattern()
//			List<String> picList = pageParser.getPicturesUrlsList(page.getWebBody());
//			if (picList != null && picList.size() > 0) {
//				for (String pic : picList) {
//					Utils.print("### Worker thread {}: pic - {}", threadIndex, pic);
//				}
//			}
//			break;
//		default:
//			log.error("worker type is not supported.");
//			break;
//		}
//	}

	private void addToMQOrWait(String url) {
		// if add to MQ failed then wait
		long timer = QUEUE_FULL_WAIT;
		try {
			while (!MessageBroker.addMessage(url, threadIndex)) {
				Utils.print("Worker thread {}: Queue is full, waiting {} ms (totally waited {} ms )...", threadIndex,
						QUEUE_FULL_WAIT, timer);

				// sleep some seconds and try until there are more than half spaces in the queue
				Thread.sleep(QUEUE_FULL_WAIT);
				timer += QUEUE_FULL_WAIT;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
