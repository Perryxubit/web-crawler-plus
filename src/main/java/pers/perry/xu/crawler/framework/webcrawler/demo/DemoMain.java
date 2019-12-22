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
		configuration.setMaxThreadNumber(1);
		configuration.setParser(new DemoPageParser());

		CrawlerController controller = new CrawlerController(configuration);
		controller.startCrawler();
	}
}
