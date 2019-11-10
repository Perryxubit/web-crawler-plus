package pers.perry.xu.crawler.framework.webcrawler.controller;

import java.nio.file.Paths;
import java.util.ArrayList;

import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.worker.CrawlerWorkerEngine;

public class CrawlerController {

	private ArrayList<String> seedList;

	public CrawlerController() {
		seedList = new ArrayList<String>();
	}

	public void startCrawler() {
		CrawlerWorkerEngine engine = new CrawlerWorkerEngine(this, Paths.get(""));
		engine.startWorkers(5);

		MessageBroker.addMessage("ssss");
	}

	public void addSeed(String url) {
		seedList.add(url);
	}

	public void deleteSeed() {
		seedList = new ArrayList<String>();
	}

	public ArrayList<String> getSeedList() {
		return seedList;
	}
}
