package pers.perry.xu.crawler.framework.webcrawler;

import pers.perry.xu.crawler.framework.webcrawler.controller.CrawlerController;

public class App {
	public static void main(String[] args) {
		CrawlerController controller = new CrawlerController();
		controller.addSeed("http://www.mmonly.cc/mmtp/");
		controller.startCrawler();
	}
}
