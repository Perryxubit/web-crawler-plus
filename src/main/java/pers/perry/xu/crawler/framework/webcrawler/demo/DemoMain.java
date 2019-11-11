package pers.perry.xu.crawler.framework.webcrawler.demo;

import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.controller.CrawlerController;

public class DemoMain {
	public static void main(String[] args) {

		CrawlerConfiguration configuration = new CrawlerConfiguration();
		configuration.addSeed("http://www.mmonly.cc/mmtp/list_9_1.html");
		configuration.addSeed("http://www.mmonly.cc/mmtp/list_9_2.html");
		configuration.setMaxThreadNumber(3);
		configuration.setParser(new DemoPageParser());

		CrawlerController controller = new CrawlerController(configuration);
		controller.startCrawler();
	}
}
