package pers.perry.xu.crawler.framework.webcrawler.demo;

import org.apache.log4j.PropertyConfigurator;

import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.controller.CrawlerController;

public class DemoMain {
	public static void main(String[] args) {
		PropertyConfigurator.configure("src/resources/log4j.properties");

		CrawlerConfiguration configuration = new CrawlerConfiguration();
//		configuration.addSeed("http://www.mmonly.cc/mmtp/list_9_1.html");
//		configuration.addSeed("http://www.mmonly.cc/mmtp/xgmn/303995.html");
		configuration.addSeed("https://www.tgbus.com/news/63281");
		configuration.setMaxThreadNumber(2);
		configuration.setWorkSpace("D:\\Development Testing & Script\\web-crawler-plus\\");
//		configuration.setParser(new DemoPageParser());
		configuration.setParser(new TGBusPageParser());

		CrawlerController controller = new CrawlerController(configuration);
		controller.startCrawler();
	}
}
