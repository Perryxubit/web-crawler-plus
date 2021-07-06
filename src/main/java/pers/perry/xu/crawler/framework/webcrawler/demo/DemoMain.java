package pers.perry.xu.crawler.framework.webcrawler.demo;

import org.apache.log4j.PropertyConfigurator;

import pers.perry.xu.crawler.framework.webcrawler.Impl.lianjia.LianJiaCrawler;
import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
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
		/*
		CrawlerConfiguration configuration = new CrawlerConfiguration();
//		configuration.addSeed("http://www.mmonly.cc/ktmh/dmmn/93212.html");
//		configuration.addSeed("https://www.tgbus.com/news/63281");
		configuration.addSeed("https://bj.lianjia.com/ershoufang/l1l2a2a3p4p5rs%E6%9C%9D%E9%98%B3/");
		configuration.setOutputMode(DataOutputMode.PrintInConsole);
		configuration.setMaxThreadNumber(3);
		configuration.setWorkSpace("/Users/puxu/workspace/github/work/");
//		configuration.setParser(new DemoPageParser());
		configuration.setParser(new LianJiaParser());
		*/

		// lian jia crawler
		CrawlerConfiguration configuration = new LianJiaCrawler().getCrawlerConfig();

		// # 2. start crawler
		CrawlerController controller = new CrawlerController(configuration);
		controller.startCrawler();
	}
}
