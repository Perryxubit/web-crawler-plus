package pers.perry.xu.crawler.framework.webcrawler.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import pers.perry.xu.crawler.framework.webcrawler.log.CrawlerLog;
import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.parser.PageParser;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

public class CrawlerWorker implements Runnable {

	private int threadIndex;
	private PageParser pageParser;
	private CrawlerLog crawlerLogging;

	CrawlerWorker(int index, PageParser pageParser, CrawlerLog crawlerLogging) {
		this.threadIndex = index;
		this.pageParser = pageParser;
		this.crawlerLogging = crawlerLogging;

		Utils.print("Worker thread {} is created and running...", threadIndex);
	}

	public void run() {
		// we need to first make sure crawlerController is not null!
		while (true) {
			String nextTargetUrl = MessageBroker.getMessage(threadIndex);
			crawlerLogging.addToHistory(nextTargetUrl);
			Utils.print("Worker thread {}: running crawler on next url: {}", threadIndex, nextTargetUrl);

			try {
				// 1. get url whole page string

				// https://www.ibm.com/developerworks/cn/java/j-lo-jsouphtml/index.html
				Document doc = Jsoup.connect(nextTargetUrl).get();
				String title = doc.title();
//				Utils.print("Worker thread {}: Title {}", threadIndex, title);

				// 2. check if we have pageParser.subUrlSeedPattern(). -> if yes, add to the MQ
				String urlPattern = pageParser.visitUrlPattern();
				List<String> urlList = getMatchingList(urlPattern, doc.body().toString());
				for (String url : urlList) {
					if (!crawlerLogging.isInHistory(url)) { // only add new url
						Utils.print("Worker thread {}: found sub seed {}", threadIndex, url);
						MessageBroker.addMessage(url, threadIndex);
						crawlerLogging.addToHistory(url);
					}
				}

				// 3. check if we have pageParser.visitTextPattern()
//				String textPattern = pageParser.visitTextPattern();
//				List<String> textList = getMatchingList(textPattern, doc.body().toString());
//				for (String string : textList) {
//					System.out.println(string);
//				}

				// 4. check if we have pageParser.visitPicturePattern()
			} catch (Exception e) {
				// TODO: handle exception
			}
			Utils.print("Worker thread {}: {} is done.", threadIndex, nextTargetUrl);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> getMatchingList(String pattern, String html) {
		List<String> targetList = new ArrayList<String>();

		Pattern webPattern = Pattern.compile(pattern);
		Matcher webMatcher = webPattern.matcher(html);
		while (webMatcher.find()) {
			targetList.add(webMatcher.group());
		}

		return targetList;
	}

}
