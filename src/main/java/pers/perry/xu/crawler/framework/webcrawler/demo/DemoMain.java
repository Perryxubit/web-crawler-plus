package pers.perry.xu.crawler.framework.webcrawler.demo;

import org.apache.log4j.PropertyConfigurator;

import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration.DataOutputMode;
import pers.perry.xu.crawler.framework.webcrawler.controller.CrawlerController;

/**
 * TODO:
 * 
 * 1. handled full MQ ## done (have to give up message?)
 * 
 * 2. multi-threads write to record file @?
 * 
 */
public class DemoMain {
	public static void main(String[] args) {
		PropertyConfigurator.configure("src/resources/log4j.properties");

		// # 1. build configuration.
		CrawlerConfiguration configuration = new CrawlerConfiguration();
//		configuration.addSeed("http://www.mmonly.cc/ktmh/dmmn/93212.html");
		configuration.addSeed("https://www.tgbus.com/news/63281");
//		configuration.addSeed("http://www.mmonly.cc/mmtp/xgmn/304890.html");
		configuration.setOutputMode(DataOutputMode.DownloadToFiles);
		configuration.setMaxThreadNumber(3);
//		configuration.setWorkSpace("/Users/perry/Documents/testprogram/webcrawler/");
		configuration.setWorkSpace("D:\\Development Testing & Script\\web-crawler-plus\\");
//		configuration.setParser(new DemoPageParser());
		configuration.setParser(new TGBusPageParser());
//		configuration.setParser(new MmonlyPageParser());

		// # 2. start crawler
		CrawlerController controller = new CrawlerController(configuration);
		controller.startCrawler();
	}
}
