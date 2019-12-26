package pers.perry.xu.crawler.framework.webcrawler.demo;

import java.util.ArrayList;
import java.util.List;

import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

public class TGBusPageParser extends DemoPageParser implements WebPageParser {

	@Override
	public List<WebMedia> getMediaDataList(WebPage page) {
		List<WebMedia> list = new ArrayList<WebMedia>();
//		list.add(new WebMedia("", "", MediaType.Picture));
		return list;
	}

	@Override
	public String getText(WebPage page) {
		return null;
	}

	@Override
	public List<String> getSeedUrlsList(WebPage page) {
		String baseStr = page.getWebBody().getElementById("relateNews").toString();
		List<String> list = new ArrayList<String>();
		list.add("https://www.tgbus.com/news/[0-9]+");
		return getMatchingList(list, baseStr);
	}

}
